package com.software5000.base.plugins;

import com.software5000.base.UserDefaultZimpl;
import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.*;

/**
 * Created by cc on 2016/12/22.
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PermissionHelper implements Interceptor {

    private static final Log log = LogFactory.getLog(PermissionHelper.class);

    private static final ThreadLocal<Long> IGNORE_PERMISSION = new ThreadLocal<Long>();
    private static final ThreadLocal<Long> IGNORE_PERMISSION_BEGIN_AND_END = new ThreadLocal<>();
    private static final ThreadLocal<String> IGNORE_PERMISSION_TAG = new ThreadLocal<String>();
    private static final ThreadLocal<Long> IGNORE_JOIN_TABLE_PERMISSION_BEGIN_AND_END = new ThreadLocal<>();//忽略掉子表权限
    private static final ThreadLocal<Long> IGNORE_JOIN_TABLE_PERMISSION = new ThreadLocal<>();//忽略掉子表权限
    private static int MAPPED_STATEMENT_INDEX = 0;
    private static int PARAMETER_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (IGNORE_PERMISSION_BEGIN_AND_END.get() == null) {
            if (IGNORE_PERMISSION.get() == null) {
                processIntercept(invocation);
            } else {
                IGNORE_PERMISSION.remove();
            }
        } else {
            //同时使用则以ignorePermissionBeginAndEnd为主，并将ignorePermission抹除
            if (IGNORE_PERMISSION.get() != null) {
                IGNORE_PERMISSION.remove();
            }
        }

        return invocation.proceed();
    }

    public static void ignorePermissionThisTime() {
        IGNORE_PERMISSION.set(System.currentTimeMillis());
    }

    public static void ignorePermissionThisTimeBegin() {
        IGNORE_PERMISSION_BEGIN_AND_END.set(System.currentTimeMillis());
    }

    public static void ignorePermissionThisTimeEnd() {
        IGNORE_PERMISSION_BEGIN_AND_END.remove();
    }

    public static void ignoreJoinTablePermissionThisTimeBegin() {
        IGNORE_JOIN_TABLE_PERMISSION_BEGIN_AND_END.set(System.currentTimeMillis());
    }

    public static void ignoreJoinTablePermissionThisTimeEnd() {
        IGNORE_JOIN_TABLE_PERMISSION_BEGIN_AND_END.remove();
    }

    public static void ignoreJoinTablePermissionThisTime() {
        IGNORE_JOIN_TABLE_PERMISSION.set(System.currentTimeMillis());
    }


    public static void ignorePermissionTagSet(String tag) {
        IGNORE_PERMISSION_TAG.set(tag);
    }

    public static void ignorePermissionTagRemove() {
        IGNORE_PERMISSION_TAG.remove();
    }

    private void processIntercept(Invocation invocation) {
        try {
            // 获取当前用户角色
//            UserDefaultZimpl principal = new UserDefaultZimpl();
//            principal.setUserType(Constant.UserType.MERCHANT);
//            BaseEntity baseEntity = new BaseEntity();
//            baseEntity.setId(61);
//            principal.setRealEntity(baseEntity);
            UserDefaultZimpl principal = new UserDefaultZimpl();
            List<PermissionRule> rules = PermissionRule.getAllRules();
            if (rules == null || rules.size() == 0 || principal == null) {
                return; // 没有规则就退出
            }

            MappedStatement ms = (MappedStatement) invocation.getArgs()[MAPPED_STATEMENT_INDEX];
            Object parameter = invocation.getArgs()[PARAMETER_INDEX];
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = boundSql.getSql().trim();

            if (SqlCommandType.UPDATE == ms.getSqlCommandType()) {
                sql = processUpdateSql(sql, rules, principal);
            } else if (SqlCommandType.DELETE == ms.getSqlCommandType()) {
                sql = processDeleteSql(sql, rules, principal);
            } else if (SqlCommandType.SELECT == ms.getSqlCommandType()) {
                sql = processSelectSql(sql, rules, principal);
            }

            BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),
                    sql, boundSql.getParameterMappings(), boundSql
                    .getParameterObject());
            for (ParameterMapping mapping : boundSql.getParameterMappings()) {
                String prop = mapping.getProperty();
                if (boundSql.hasAdditionalParameter(prop)) {
                    newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
                }
            }
            MappedStatement newMs = copyFromMappedStatement(ms,
                    new BoundSqlSqlSource(newBoundSql));
            invocation.getArgs()[MAPPED_STATEMENT_INDEX] = newMs;

        } catch (Exception e) {
            log.error("处理Sql出错！", e);
        }
    }

    private String processSelectSql(String sql, List<PermissionRule> rules, UserDefaultZimpl principal) {
        try {
            String replaceSql = null;
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            String mainTable = null;
            if (selectBody.getFromItem() instanceof Table) {
                mainTable = ((Table) selectBody.getFromItem()).getName().replace("`", "");
            } else if (selectBody.getFromItem() instanceof SubSelect) {
                replaceSql = processSelectSql(((SubSelect) selectBody.getFromItem()).getSelectBody().toString(), rules, principal);
            }
            if (!ValidUtil.isEmpty(replaceSql)) {
                sql = sql.replace(((SubSelect) selectBody.getFromItem()).getSelectBody().toString(), replaceSql);
            }
            String mainTableAlias = mainTable;
            try {
                mainTableAlias = selectBody.getFromItem().getAlias().getName();
            } catch (Exception e) {
                log.debug("当前sql中， " + mainTable + " 没有设置别名");
            }

            String condExpr = null;
            PermissionRule realRuls = null;
            Map<String, PermissionRule> matchingMap = getMatchingRule(rules, principal.getRoles()); //满足的规则信息
            for (String key : matchingMap.keySet()) {
                PermissionRule rule = matchingMap.get(key);
                if (rule.getFromEntity().indexOf("," + mainTable + ",") != -1) {
                    // 若主表匹配规则主体，则直接使用本规则
                    realRuls = rule;

                    condExpr = rule.getExps().replace("{uid}", principal.getRealEntity().getId().toString()).replace("{me}", mainTable).replace("{me.a}", mainTableAlias);
                    if (selectBody.getWhere() == null) {
                        selectBody.setWhere(CCJSqlParserUtil.parseCondExpression(condExpr));
                    } else {
                        AndExpression and = new AndExpression(selectBody.getWhere(), CCJSqlParserUtil.parseCondExpression(condExpr));
                        selectBody.setWhere(and);
                    }
                }

                if (IGNORE_JOIN_TABLE_PERMISSION_BEGIN_AND_END.get() == null && IGNORE_JOIN_TABLE_PERMISSION.get() == null) {
                    try {
                        String joinTable = null;
                        String joinTableAlias = null;
                        for (Join j :
                                selectBody.getJoins()) {
                            if (rule.getFromEntity().indexOf("," + ((Table) j.getRightItem()).getName() + ",") != -1) {
                                // 当主表不能匹配时，匹配所有join，使用符合条件的第一个表的规则。
                                realRuls = rule;
                                joinTable = ((Table) j.getRightItem()).getName();
                                joinTableAlias = j.getRightItem().getAlias().getName();

                                condExpr = rule.getExps().replace("{uid}", principal.getRealEntity().getId().toString()).replace("{me}", joinTable).replace("{me.a}", joinTableAlias);
                                if (j.getOnExpression() == null) {
                                    j.setOnExpression(CCJSqlParserUtil.parseCondExpression(condExpr));
                                } else {
                                    AndExpression and = new AndExpression(j.getOnExpression(), CCJSqlParserUtil.parseCondExpression(condExpr));
                                    j.setOnExpression(and);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.debug("当前sql没有join的部分！");
                    }
                }
            }

            if (IGNORE_JOIN_TABLE_PERMISSION.get() != null) {
                IGNORE_JOIN_TABLE_PERMISSION.remove();
            }

            if (realRuls == null) {
                return sql; // 没有合适规则直接退出。
            }

            if (sql.indexOf("limit ?,?") != -1 && select.toString().indexOf("LIMIT ? OFFSET ?") != -1) {
                sql = select.toString().replace("LIMIT ? OFFSET ?", "limit ?,?");
            } else {
                sql = select.toString();
            }

        } catch (JSQLParserException e) {
            log.error("change sql error .", e);
        }
        return sql;
    }

    private String processUpdateSql(String sql, List<PermissionRule> rules, UserDefaultZimpl principal) {
        try {
            Update stm = (Update) CCJSqlParserUtil.parse(sql);
            String mainTable = stm.getTables().get(0).getName().replace("`", "");
            String mainTableAlias = mainTable;
            try {
                mainTableAlias = stm.getTables().get(0).getAlias().getName();
            } catch (Exception e) {
                log.debug("当前sql中， " + mainTable + " 没有设置别名");
            }

            PermissionRule realRuls = null;
            Map<String, PermissionRule> matchingMap = getMatchingRule(rules, principal.getRoles()); //满足的规则信息
            lv1:
            for (String key : matchingMap.keySet()) {
                PermissionRule rule = matchingMap.get(key);
                try {
                    for (Table t : stm.getTables()) {
                        if (rule.getFromEntity().indexOf("," + t.getName() + ",") != -1) {
                            // 匹配主表
                            realRuls = rule;
                            mainTable = t.getName();
                            mainTableAlias = t.getAlias().getName();
                            break lv1;
                        }
                    }
                    if (IGNORE_JOIN_TABLE_PERMISSION_BEGIN_AND_END.get() == null || IGNORE_JOIN_TABLE_PERMISSION == null) {
                        for (Join j :
                                stm.getJoins()) {
                            if (rule.getFromEntity().indexOf("," + ((Table) j.getRightItem()) + ",") != -1) {
                                // 当主表不能匹配时，匹配所有join，使用符合条件的第一个表的规则。
                                realRuls = rule;
                                mainTable = ((Table) j.getRightItem()).getName();
                                mainTableAlias = j.getRightItem().getAlias().getName();
                                break lv1;
                            }
                        }
                    }
                    if (IGNORE_JOIN_TABLE_PERMISSION != null) {
                        IGNORE_JOIN_TABLE_PERMISSION.remove();
                    }
                } catch (Exception e) {
                    log.debug("当前sql没有join的部分！");
                }
            }
            if (realRuls == null) {
                return sql; // 没有合适规则直接退出。
            }

            String condExpr = realRuls.getExps().replace("{uid}", principal.getRealEntity().getId().toString()).replace("{me}", mainTable).replace("{me.a}", mainTableAlias);
            if (stm.getWhere() == null) {
                stm.setWhere(CCJSqlParserUtil.parseCondExpression(condExpr));
            } else {
                AndExpression and = new AndExpression(stm.getWhere(), CCJSqlParserUtil.parseCondExpression(condExpr));
                stm.setWhere(and);
            }

            sql = stm.toString();
        } catch (JSQLParserException e) {
            log.error("change sql error .", e);
        }
        return sql;
    }

    private String processDeleteSql(String sql, List<PermissionRule> rules, UserDefaultZimpl principal) {
        try {
            Delete stm = (Delete) CCJSqlParserUtil.parse(sql);
            String mainTable = stm.getTable().getName().replace("`", "");
            String mainTableAlias = mainTable;
            try {
                mainTableAlias = stm.getTable().getAlias().getName();
            } catch (Exception e) {
                log.debug("当前sql中， " + mainTable + " 没有设置别名");
            }

            PermissionRule realRuls = null;
            Map<String, PermissionRule> matchingMap = getMatchingRule(rules, principal.getRoles()); //满足的规则信息
            lv1:
            for (String key : matchingMap.keySet()) {
                PermissionRule rule = matchingMap.get(key);
                try {
                    if (rule.getFromEntity().indexOf("," + stm.getTable().getName() + ",") != -1) {
                        // 当主表不能匹配时，匹配所有join，使用符合条件的第一个表的规则。
                        realRuls = rule;
                        mainTable = stm.getTable().getName();
                        mainTableAlias = stm.getTable().getAlias().getName();
                        break lv1;
                    }
                } catch (Exception e) {
                    log.debug("当前sql没有join的部分！");
                }
            }
            if (realRuls == null) {
                return sql; // 没有合适规则直接退出。
            }

            String condExpr = realRuls.getExps().replace("{uid}", principal.getRealEntity().getId().toString()).replace("{me}", mainTable).replace("{me.a}", mainTableAlias);
            if (stm.getWhere() == null) {
                stm.setWhere(CCJSqlParserUtil.parseCondExpression(condExpr));
            } else {
                AndExpression and = new AndExpression(stm.getWhere(), CCJSqlParserUtil.parseCondExpression(condExpr));
                stm.setWhere(and);
            }

            sql = stm.toString();
        } catch (JSQLParserException e) {
            log.error("change sql error .", e);
        }
        return sql;
    }

    /**
     * 获取匹配的规则
     *
     * @return
     */
    private Map<String, PermissionRule> getMatchingRule(List<PermissionRule> rules, Set<String> roles) {
        //匹配的规则
        Map<String, PermissionRule> matchingMap = new HashMap<String, PermissionRule>();
        if (rules != null && roles != null) {
            for (PermissionRule rule : rules) {
                if (rule != null) {
                    for (String roleStr : roles) {
                        if (rule.getRoles().indexOf("," + roleStr + "_" + IGNORE_PERMISSION_TAG.get() + ",") != -1) {
                            matchingMap.put("," + roleStr + "," + rule.getFromEntity(), rule);
                        } else if (rule.getRoles().indexOf("," + roleStr + ",") != -1 && matchingMap.get(rule.getRoles() + rule.getFromEntity()) == null) {
                            matchingMap.put(rule.getRoles() + rule.getFromEntity(), rule);
                        }
                    }
                }
            }
        }
        return matchingMap;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                    SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms
                .getConfiguration(), ms.getId(), newSqlSource, ms
                .getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        try {
            builder.keyProperty(ms.getKeyProperties()[0]);
        } catch (Exception e) {
            builder.keyProperty(null);
        }

        // setStatementTimeout()
        builder.timeout(ms.getTimeout());

        // setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        // setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        // setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        //nothing
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
