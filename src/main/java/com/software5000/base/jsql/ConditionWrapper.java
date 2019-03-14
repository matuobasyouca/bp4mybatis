package com.software5000.base.jsql;

import com.software5000.base.BaseEntity;
import com.software5000.util.JsqlFieldException;
import com.software5000.util.JsqlUtils;
import com.zscp.master.util.ClassUtil;
import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;

import java.util.HashSet;
import java.util.Set;

/**
 * 封装查询条件类
 * @param <T> BaseEntity实体
 */
public class ConditionWrapper<T extends BaseEntity> {

    private T entity;
    private AndExpressionList andExpressionList;
    private Set<String> needCleanFields = new HashSet<>();

    /**
     * 需要传入实体用于初始化类
     * @param entity
     */
    public ConditionWrapper(T entity) {
        this.entity = entity;
        this.andExpressionList = new AndExpressionList();
    }

    /**
     * 获取拼接后的查询条件,并且清除对应栏位的值
     *
     * @return
     */
    public Expression get() {
        cleanEntityValue();
        return this.andExpressionList.get();
    }

    /**
     * 当设置一个条件表达式时，需要清除对应的实体中的存值。
     */
    private void cleanEntityValue() {
        String fieldName = null;
        try {
            for (String fn : this.needCleanFields) {
                fieldName = fn;
                ClassUtil.setValueByField(this.entity, fieldName, null);
            }
        } catch (Exception e) {
            throw new JsqlFieldException("the fieldname : [" + fieldName + "] not exist in class [" + this.entity.getClass().getName() + "]");
        }
    }
    // region condition 条件区

    /**
     * 等于，并且直接使用实体中对应的参数
     *
     * @param fieldName 字段名称
     * @return 当前对象本身
     */
    public ConditionWrapper eq(String fieldName) {
        return this.eq(fieldName, null);
    }

    /**
     * 等于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper eq(String fieldName, Expression value) {
        if (!ValidUtil.valid(value)) {
            value = JsqlUtils.getColumnValueFromEntity(this.entity, fieldName);
        }

        this.andExpressionList.append(JsqlUtils.equalTo(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }

    /**
     * 大于，并且直接使用实体中对应的参数
     *
     * @param fieldName 字段名称
     * @return 当前对象本身
     */
    public ConditionWrapper gt(String fieldName) {
        return this.gt(fieldName, null);
    }

    /**
     * 大于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper gt(String fieldName, Expression value) {
        if (!ValidUtil.valid(value)) {
            value = JsqlUtils.getColumnValueFromEntity(this.entity, fieldName);
        }

        this.andExpressionList.append(JsqlUtils.greaterThan(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }

    /**
     * 大于等于，并且直接使用实体中对应的参数
     *
     * @param fieldName 字段名称
     * @return 当前对象本身
     */
    public ConditionWrapper ge(String fieldName) {
        return this.ge(fieldName, null);
    }

    /**
     * 大于等于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper ge(String fieldName, Expression value) {
        if (!ValidUtil.valid(value)) {
            value = JsqlUtils.getColumnValueFromEntity(this.entity, fieldName);
        }

        this.andExpressionList.append(JsqlUtils.greaterThanEquals(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }


    /**
     * 小于，并且直接使用实体中对应的参数
     *
     * @param fieldName 字段名称
     * @return 当前对象本身
     */
    public ConditionWrapper lt(String fieldName) {
        return this.lt(fieldName, null);
    }

    /**
     * 小于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper lt(String fieldName, Expression value) {
        if (!ValidUtil.valid(value)) {
            value = JsqlUtils.getColumnValueFromEntity(this.entity, fieldName);
        }

        this.andExpressionList.append(JsqlUtils.lessThan(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }

    /**
     * 小于等于，并且直接使用实体中对应的参数
     *
     * @param fieldName 字段名称
     * @return 当前对象本身
     */
    public ConditionWrapper le(String fieldName) {
        return this.le(fieldName, null);
    }

    /**
     * 小于等于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper le(String fieldName, Expression value) {
        if (!ValidUtil.valid(value)) {
            value = JsqlUtils.getColumnValueFromEntity(this.entity, fieldName);
        }

        this.andExpressionList.append(JsqlUtils.lessThanEquals(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }
    /**
     * 小于等于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper in(String fieldName, ItemsList value) {
        if (!ValidUtil.valid(value)) {
            throw new JsqlFieldException("the 'in' condition can't query with null value");
        }

        this.andExpressionList.append(JsqlUtils.in(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }
    /**
     * 小于等于，并且给定参数，如果参数为空，会使用实体中的参数
     *
     * @param fieldName 字段名称
     * @param value     指定值
     * @return 当前对象本身
     */
    public ConditionWrapper notIn(String fieldName, ItemsList value) {
        if (!ValidUtil.valid(value)) {
            throw new JsqlFieldException("the 'in' condition can't query with null value");
        }

        this.andExpressionList.append(JsqlUtils.notIn(new Column(JsqlUtils.transCamelToSnake(fieldName)), value));
        needCleanFields.add(fieldName);
        return this;
    }
    // endregion

}
