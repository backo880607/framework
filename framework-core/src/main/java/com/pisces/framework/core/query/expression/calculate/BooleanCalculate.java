package com.pisces.framework.core.query.expression.calculate;

import lombok.Getter;

/**
 * 布尔计算
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class BooleanCalculate implements Calculate {
    private boolean value;

    @Override
    public int parse(String str, int index) {
        return 0;
    }

    @Override
    public Class<?> getReturnClass() {
        return Boolean.class;
    }
}
