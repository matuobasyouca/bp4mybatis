package com.software5000.base.plugins;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

public class MybatisNamedParameter  extends ASTNodeAccessImpl implements Expression {
    private String name;

    public MybatisNamedParameter(String name) {
        this.name = name;
    }

    /**
     * The name of the parameter
     *
     * @return the name of the parameter
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "#{" + name + "}";
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
//        expressionVisitor.visit(this);
    }
}
