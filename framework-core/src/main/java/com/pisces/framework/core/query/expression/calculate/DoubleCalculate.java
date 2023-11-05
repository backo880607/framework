package com.pisces.framework.core.query.expression.calculate;

import lombok.Getter;

/**
 * 两倍计算
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class DoubleCalculate implements Calculate {
    private final double value;

    public DoubleCalculate(double value) {
        this.value = value;
    }

    @Override
    public int parse(String str, int index) {
        return -1;
    }

    @Override
    public Class<?> getReturnClass() {
        return Double.class;
    }
}
