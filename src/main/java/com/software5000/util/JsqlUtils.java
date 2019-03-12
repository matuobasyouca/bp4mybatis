package com.software5000.util;

import com.google.common.base.CaseFormat;
import com.software5000.base.BaseDaoNew;
import com.software5000.base.NotDatabaseField;
import com.zscp.master.util.DateUtils;
import com.zscp.master.util.RegUtil;
import com.zscp.master.util.StringUtil;
import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JsqlUtils {

    /**
     * 从给定的类中获取对应的数据库字段名称
     *
     * @param objClass 目标类
     * @return 列数组
     */
    public static List<Column> getAllColumnNamesFromEntity(Class<?> objClass) {
        /**
         * 是否包含父类字段
         */

        List<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(objClass.getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(objClass.getDeclaredFields()));

        return JsqlUtils.getAllColumnNamesFromEntityExceptSome(objClass, null);
    }


    public static List<Column> getAllColumnNamesFromEntityExceptSome(Class<?> objClass, List<String> exceptColumnNames) {
        List<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(objClass.getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(objClass.getDeclaredFields()));

        return fields.stream()
                .filter(f -> (f.getAnnotation(NotDatabaseField.class) == null))
                .filter(e -> ValidUtil.valid(exceptColumnNames) ? !exceptColumnNames.contains(e.getName()) : true)
                .map(f -> new Column(JsqlUtils.transCamelToSnake(f.getName())))
                .collect(Collectors.toList());

    }

    /**
     * 获取数据库列对应实体的字段值列表
     *
     * @param entity  实体
     * @param columns 数据库列
     * @return 字段值列表
     */
    public static ExpressionList getAllColumnValueFromEntity(Object entity, List<Column> columns) {

        List<Expression> expressions = new ArrayList<>();
        for (Column column : columns) {
            expressions.add(JsqlUtils.getColumnValueFromEntity(entity, column.getColumnName()));
        }
        return new ExpressionList(expressions);
    }

    /**
     * 获取指定的列以及对应的值
     *
     * @param entity         带值实体
     * @param namedCols      指定列
     * @param isSupportBlank 是否包含空字符值的列，true为包含
     * @param isSupportNull  是否包含Null值的列，true为包含
     * @return 对象数组2个值，结果1：有值的列；结果2：对应顺序列的值
     */
    public static Object[] getNamedColumnAndValueFromEntity(Object entity, Column[] namedCols, boolean isSupportBlank, boolean isSupportNull) {
        List<Column> resultColumns = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        if (!ValidUtil.valid(namedCols)) {
            namedCols = new Column[0];
            namedCols = getAllColumnNamesFromEntity(entity.getClass()).toArray(namedCols);
        }

        for (Column column : namedCols) {
            Expression expression = JsqlUtils.getColumnValueFromEntity(entity, column.getColumnName());

            // 确认null是否接收
            if (!isSupportNull && expression instanceof NullValue) {
                continue;
            }

            // 确认空字符串接收
            if (expression instanceof StringValue && !isSupportBlank && !ValidUtil.valid(expression)) {
                continue;
            }

            resultColumns.add(column);
            expressions.add(JsqlUtils.getColumnValueFromEntity(entity, column.getColumnName()));
        }

        return new Object[]{resultColumns, expressions};
    }

    /**
     * 根据给出的实体，获取对应字段的值
     *
     * @param entity
     * @param fieldName
     * @return 单个字段值
     */
    public static Expression getColumnValueFromEntity(Object entity, String fieldName) {
        if (fieldName.indexOf("_") != -1) {
            fieldName = JsqlUtils.transSnakeToCamel(fieldName);
        }
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
     *
     * @param value 字段值
     * @return JSqlParser中的标准类型值
     */
    private static Expression convertValueType(Object value) {
        if (value == null) {
            return new NullValue();
        }

        if (value instanceof Double || value instanceof Float) {
            return new DoubleValue(String.valueOf(value));
        } else if (value instanceof Long || value instanceof Short
                || value instanceof Integer || value instanceof BigInteger) {
            return new LongValue(String.valueOf(value));
        } else if (value instanceof Timestamp) {
            return new TimestampValue(String.valueOf(value));
        } else if (value instanceof Time) {
            return new TimeValue(String.valueOf(value));
        } else if (value instanceof java.util.Date || value instanceof java.sql.Date) {
            // A Date in the form {d 'yyyy-mm-dd'}
            return new DateValue(DateUtils.formatDate((java.sql.Date) value, "yyyy-MM-dd"));
        } else {
            // 字符串要做防注入处理
            return new StringValue(String.valueOf(value).trim().replace("'", "\\'"));
        }
    }

    public static List<OrderByElement> getOrderByElementFromString(String orderBy) {
        List<OrderByElement> orderByElements = new ArrayList<>();

        String[] orderByStrings = orderBy.split(",");
        for (String orderByString : orderByStrings) {
            if (orderByString.toLowerCase().matches("[^ ]+( +asc)?") ||
                    orderByString.toLowerCase().matches("[^ ]+( +desc)?")) {

                String[] o = orderByString.split(" +");
                switch (o.length) {
                    case 1 ：
                        orderByElements.add(new OrderByElement())
                        break;
                    case 2 :
                        break;
                        default:
                            b
                }
            }
        }

        return orderByElements;
    }

    /**
     * 返回一个等式过滤条件
     *
     * @param column 列名
     * @param value  值
     * @return 等式过滤条件 如 1=1
     */
    public static Expression equalTo(Column column, Expression value) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(column);
        equalsTo.setRightExpression(value);
        return equalsTo;
    }

    /**
     * 骆驼转蛇
     *
     * @return 转换后的字符串
     */
    public static String transCamelToSnake(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    /**
     * 蛇转骆驼
     *
     * @return 转换后的字符串
     */
    public static String transSnakeToCamel(String name) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

    public static void main(String[] args) {
        List<String> s = new ArrayList<>();
        com.zscp.master.util.RegUtil.getAllMatchValues("[^ ]+( +asc)?","a asc",1,s);
        System.out.println(String.valueOf("a asc".matches("[^ ]+( +asc)?")));
        System.out.println(String.valueOf("asd".matches("[^ ]+( +asc)?")));
        System.out.println(String.valueOf("asd asx".matches("[^ ]+( +asc)?")));
        System.out.println("abc   def".split(" "));
        System.out.println("abc   def".split(" +"));
        System.out.println(s);
    }
}
