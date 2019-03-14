package com.software5000.base.plugins;

import com.software5000.util.JsqlUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通用的底层数据处理
 * 1. 新增时，自动添加 新增和修改时间 为当前时间
 * 2. 修改时，自动添加 修改时间 为当前时间
 * 3. 查询时，自动去除 查询条件中的 1=1 条件
 *
 * @author matuobasyouca@gmail.com
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class CommonInterceptor implements Interceptor {

    public static final String UPDATE_TIME_FIELD_NAME = JsqlUtils.transCamelToSnake("updateTime");
    public static final String CREATE_TIME_FIELD_NAME = JsqlUtils.transCamelToSnake("createTime");
    public static final String ID_FIELD_NAME = "id";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final ThreadLocal<Long> IGNORE_DATA = new ThreadLocal<Long>();

    private Properties props = null;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (IGNORE_DATA.get() == null) {
            processIntercept(invocation);
        } else {
            IGNORE_DATA.remove();
        }
        return invocation.proceed();
    }

    public Object processIntercept(Invocation invocation) throws Throwable {
        String interceptMethod = invocation.getMethod().getName();
        if (!"prepare".equals(interceptMethod)) {
            return invocation.proceed();
        }

        StatementHandler handler = (StatementHandler) PluginUtil.processTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        SqlCommandType sqlCmdType = ms.getSqlCommandType();
        if (sqlCmdType != SqlCommandType.UPDATE && sqlCmdType != SqlCommandType.INSERT) {
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();
        //获取原始sql
        String originalSql = (String) metaObject.getValue("delegate.boundSql.sql");
        logger.debug("==> originalSql: " + originalSql);
        //追加参数
        String newSql = "";
        if (sqlCmdType == SqlCommandType.UPDATE) {
            newSql = setTimeForUpdate(originalSql);
        } else if (sqlCmdType == SqlCommandType.INSERT) {
            newSql = setTimeForInsert(originalSql);
        }
        //修改原始sql
        if (newSql.length() > 0) {
            logger.debug("==> newSql after change create/update time : " + newSql);
            metaObject.setValue("delegate.boundSql.sql", newSql);
        }
        return invocation.proceed();
    }


    private String setTimeForInsert(String sqls) {

        int createTimeIndex = -1;
        int updateTimeIndex = -1;
        try {
            Statement stmt = CCJSqlParserUtil.parse(sqls);
            Insert insert = (Insert) stmt;
            List<Column> columns = insert.getColumns();
            // 循环所有列，检查是否存在 createTime和updateTime两个字段，并且获取这两个字段的序号
            for (int ci = 0; ci < columns.size(); ci++) {
                if (CREATE_TIME_FIELD_NAME.equalsIgnoreCase(columns.get(ci).getColumnName())) {
                    createTimeIndex = ci;
                }
                if (UPDATE_TIME_FIELD_NAME.equalsIgnoreCase(columns.get(ci).getColumnName())) {
                    updateTimeIndex = ci;
                }

                if (createTimeIndex > -1 && updateTimeIndex > -1) {
                    break;
                }
            }
            ItemsList itemList = insert.getItemsList();

            // 确认是处理单条数据还是数据列表
            if (itemList instanceof ExpressionList) {
                ((ExpressionList) itemList).getExpressions().set(createTimeIndex, new TimestampValue(String.valueOf(TIMESTAMP_FORMAT.format(new Date(System.currentTimeMillis())))));
                ((ExpressionList) itemList).getExpressions().set(updateTimeIndex, new TimestampValue(String.valueOf(TIMESTAMP_FORMAT.format(new Date(System.currentTimeMillis())))));
            } else if (itemList instanceof MultiExpressionList) {
                for (ExpressionList el : ((MultiExpressionList) itemList).getExprList()) {
                    el.getExpressions().set(createTimeIndex, new TimestampValue(String.valueOf(TIMESTAMP_FORMAT.format(new Date(System.currentTimeMillis())))));
                    el.getExpressions().set(updateTimeIndex, new TimestampValue(String.valueOf(TIMESTAMP_FORMAT.format(new Date(System.currentTimeMillis())))));
                }
            }
            return insert.toString();
        } catch (JSQLParserException e) {
            logger.error("set insert createTime/updateTime error!", e);
        }

        return null;
    }

    private String setTimeForUpdate(String sqls) {
        try {
            Statement parse = CCJSqlParserUtil.parse(sqls);
            if (parse instanceof Update) {
                List<Column> removedColumns = new ArrayList<>();
                List<Expression> removedExpression = new ArrayList<>();
                Update update = (Update) parse;

                for (int i = 0; i < update.getColumns().size(); i++) {
                    if (UPDATE_TIME_FIELD_NAME.equals(update.getColumns().get(i).getColumnName())
                            || CREATE_TIME_FIELD_NAME.equals(update.getColumns().get(i).getColumnName())
                            || ID_FIELD_NAME.equals(update.getColumns().get(i).getColumnName())) {

                        removedColumns.add(((Update) parse).getColumns().get(i));
                        removedExpression.add(((Update) parse).getExpressions().get(i));
                    }
                }

                update.getColumns().removeAll(removedColumns);
                update.getExpressions().removeAll(removedExpression);

                update.getColumns().add(new Column(UPDATE_TIME_FIELD_NAME));
                update.getExpressions().add(new TimestampValue(String.valueOf(TIMESTAMP_FORMAT.format(new Date(System.currentTimeMillis())))));
                return parse.toString();
            }
        } catch (JSQLParserException e) {
            logger.error("set update updateTime error!", e);
        }

        return null;
    }

    private boolean contains(List<Column> columns, String columnName) {
        if (columns == null || columns.size() <= 0) {
            return false;
        }
        if (columnName == null || columnName.length() <= 0) {
            return false;
        }
        for (Column column : columns) {
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        if (null != properties && !properties.isEmpty()) {
            props = properties;
        }
    }

}
