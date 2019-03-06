package com.software5000.util;

import com.software5000.base.BaseDaoNew;
import com.software5000.base.NotDatabaseField;
import com.software5000.base.plugins.MybatisNamedParameter;
import com.software5000.biz.entity.SystemCode;
import com.zscp.master.util.DateUtils;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JsqlUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 从给定的类中获取对应的数据库字段名称
     *
     * @param objClass       目标类
     * @param withSuperclass 是否包含父类字段
     * @return 列数组
     */
    public static List<Column> getColumnNameFromEntity(Class<?> objClass, Boolean withSuperclass) {
        List<Column> columns = new ArrayList<>();

        List<Field> fields = new ArrayList();
        if (withSuperclass) {
            fields.addAll(Arrays.asList(objClass.getSuperclass().getDeclaredFields()));
        }

        fields.addAll(Arrays.asList(objClass.getDeclaredFields()));

        return fields.stream().filter((f) -> {
            return f.getAnnotation(NotDatabaseField.class) == null;
        }).map(f -> new Column(f.getName())).collect(Collectors.toList());

    }

    /**
     * 获取数据库列对应实体的字段值列表
     * @param entity 实体
     * @param columns 数据库列
     * @return 字段值列表
     */
    public static ExpressionList getAllColumnValueFromEntity(Object entity, List<Column> columns) {

        List<Expression> expressions = new ArrayList<>();
        for (Column column : columns) {
            expressions.add(new MybatisNamedParameter(BaseDaoNew.PARAM_PREFIX + "." + column.getColumnName()));
        }
        return new ExpressionList(expressions);
    }

    /**
     * 获取数据库列对应实体的字段值列表
     * @param entity 实体
     * @param columns 数据库列
     * @return 字段值列表
     */
    public static ExpressionList getAllColumnValueFromEntityNew(Object entity, List<Column> columns) {

        List<Expression> expressions = new ArrayList<>();
        for (Column column : columns) {
            expressions.add(JsqlUtils.getColumnValueFromEntity(entity, column.getColumnName()));
        }
        return new ExpressionList(expressions);
    }

    /**
     * 根据给出的实体，获取对应字段的值
     *
     * @param entity
     * @param fieldName
     * @return 单个字段值
     */
    public static Expression getColumnValueFromEntity(Object entity, String fieldName) {
        Object returnValue;
        try {
            try {
                returnValue = entity.getClass().getDeclaredMethod("get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1)).invoke(entity);
            } catch (NoSuchMethodException nsme) {
                Method method = entity.getClass().getSuperclass().getDeclaredMethod("get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1));
                returnValue = method.invoke(entity);
            }
        } catch (Exception e) {
            returnValue = null;
        }

        return JsqlUtils.convertValueType(returnValue);
    }

    /**
     * 根据字段值的实际类型转换为JSqlParser中的标准类型
     * @param value 字段值
     * @return JSqlParser中的标准类型值
     */
    private static Expression convertValueType(Object value) {
        if (value == null) {
            return new NullValue();
        }

        if (value instanceof Double) {
            return new DoubleValue(String.valueOf(value));
        } else if (value instanceof Long) {
            return new LongValue(String.valueOf(value));
        } else if (value instanceof Timestamp) {
            return new TimestampValue(String.valueOf(value));
        } else if (value instanceof Time) {
            return new TimeValue(String.valueOf(value));
        } else if (value instanceof java.util.Date || value instanceof java.sql.Date) {
            // A Date in the form {d 'yyyy-mm-dd'}
            return new DateValue(DateUtils.formatDate((java.sql.Date) value, "yyyy-MM-dd"));
        } else {
            return new StringValue(JsqlUtils.transactSQLInjection(String.valueOf(value)));
        }
    }

    /**
     * 模仿PreparedStatement防止sql注入
     *
     * @param str 待处理字符串
     * @return 字符串
     */
    private static String transactSQLInjection(Object str) {
        return str.toString().trim().replace("'", "").replace("\\", "\\\\").replace(";", "；");
    }

    public static void main(String[] args) {
        SystemCode systemCode = new SystemCode();
        systemCode.setCodeFiter(1);
        systemCode.setCreateTime(new Timestamp(System.currentTimeMillis()));
        systemCode.setCodeName("codename");

        Object o = JsqlUtils.getColumnValueFromEntity(systemCode, "codeFiter");
        System.out.printf("sss : " + o);

    }
}
