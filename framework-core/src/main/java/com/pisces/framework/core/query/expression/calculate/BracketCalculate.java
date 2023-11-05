package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.query.expression.Expression;
import com.pisces.framework.core.query.expression.ExpressionNode;
import lombok.Getter;

import java.util.Map;

/**
 * 支架计算
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class BracketCalculate implements Calculate {
    private final Expression value = new Expression();

    @Override
    public int parse(String str, int index) {
        Map.Entry<Integer, ExpressionNode> nd = this.value.create(str, ++index, false);
        if (nd.getValue() == null) {
            return -1;
        }
        this.value.root = nd.getValue();
        return nd.getKey() + 1;
    }

    @Override
    public Class<?> getReturnClass() {
        return this.value.getReturnClass();
    }

}
