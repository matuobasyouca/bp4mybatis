package com.software5000.base.jsql;


import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

import java.util.List;

/**
 * 过滤条件组装类
 *
 * @author mauotasyouca@gmail.com
 */
public class AndExpressionList {

    private Expression singleExpression = null;
    private AndExpression andExpression = null;

    /**
     * 添加单个过滤条件表达式
     *
     * @param rightExpression 单个过滤条件
     * @return
     */
    public AndExpressionList append(Expression rightExpression) {
        if(!ValidUtil.valid(singleExpression)){
            singleExpression = rightExpression;
            return this;
        }

        if (!ValidUtil.valid(andExpression)) {
            andExpression = new AndExpression(singleExpression, rightExpression);
        } else {
            andExpression = new AndExpression(andExpression, rightExpression);
        }
        return this;
    }

    /**
     * 添加过滤条件列表
     *
     * @param expressions 过滤条件列表
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
    public Expression get() {
        return ValidUtil.valid(andExpression)?andExpression:singleExpression;
    }

}
