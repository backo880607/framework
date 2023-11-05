package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.config.CoreMessage;
import com.pisces.framework.core.exception.ExpressionException;
import com.pisces.framework.core.utils.lang.DateUtils;
import lombok.Getter;

import java.util.Date;

/**
 * 日期时间计算
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class DateTimeCalculate implements Calculate {
    private Date value;

    @Override
    public int parse(String str, int index) throws ExpressionException {
        int temp = ++index;
        while (index < str.length()) {
            if (str.charAt(index) == '#') {
                String text = str.substring(temp, index);
                this.value = DateUtils.parse(text);
                return ++index;
            }
            ++index;
        }

        throw new ExpressionException(temp, index, CoreMessage.DateTimeError, str.substring(temp, index));
    }

    @Override
    public Class<?> getReturnClass() {
        return Date.class;
    }
}
