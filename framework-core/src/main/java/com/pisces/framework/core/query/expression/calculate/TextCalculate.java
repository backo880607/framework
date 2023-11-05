package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.config.CoreMessage;
import com.pisces.framework.core.exception.ExpressionException;
import lombok.Getter;

/**
 * 文本计算
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class TextCalculate implements Calculate {
    private String value;

    @Override
    public int parse(String str, int index) throws ExpressionException {
        int temp = ++index;
        while (index < str.length()) {
            if (str.charAt(index) == '\'') {
                value = str.substring(temp, index);
                return ++index;
            }
            ++index;
        }

        throw new ExpressionException(temp, index, CoreMessage.TextError, str.substring(temp, index));
    }

    @Override
    public Class<?> getReturnClass() {
        return String.class;
    }
}
