package com.software5000.base.jsql;


import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;

import java.util.List;

/**
 * 过滤条件组装类
 *
 * @author mauotasyouca@gmail.com
 */
public class AndExpressionList {

    private AndExpression andExpression = null;

    /**
     * 添加单个过滤条件表达式
     *
     * @param rightExpression 单个过滤条件
     *
     * @return
     */
    public AndExpressionList append(Expression rightExpression) {
        if (!ValidUtil.valid(andExpression)) {
            andExpression = new AndExpression(new EqualToForever(), rightExpression);
        } else {
            andExpression = new AndExpression(andExpression, rightExpression);
        }
        return this;
    }

    /**
     * 添加过滤条件列表
     *
     * @param expressions 过滤条件列表
     *
     * @return
     */
    public AndExpression appendListAndGet(List<Expression> expressions) {

        for (Expression expression : expressions) {
            this.append(expression);
        }

        return andExpression;
    }

    /**
     * 获取最终的完整过滤条件
     *
     * @return
     */
    public AndExpression get() {
        return andExpression;
    }

}

class EqualToForever extends EqualsTo {
    public EqualToForever() {
        this.setLeftExpression(new LongValue(1));
        this.setRightExpression(new LongValue(1));
    }
}