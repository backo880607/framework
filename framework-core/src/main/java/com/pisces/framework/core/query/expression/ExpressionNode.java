package com.pisces.framework.core.query.expression;

import com.pisces.framework.core.query.expression.calculate.Calculate;

/**
 * 表达式节点
 *
 * @author jason
 * @date 2022/12/07
 */
public class ExpressionNode {
    public OperationType type;
    public Calculate calculate;
    public ExpressionNode lchild;
    public ExpressionNode rchild;

    public ExpressionNode(OperationType type, Calculate cal, ExpressionNode lc, ExpressionNode rc) {
        this.type = type;
        this.calculate = cal;
        this.lchild = lc;
        this.rchild = rc;
    }

    public <T extends Calculate> T getCalculate() {
        return (T) this.calculate;
    }
}
