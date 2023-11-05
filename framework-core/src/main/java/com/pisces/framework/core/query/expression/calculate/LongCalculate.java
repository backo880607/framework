package com.pisces.framework.core.query.expression.calculate;

import lombok.Getter;

/**
 * 长时间计算
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class LongCalculate implements Calculate {
    private final long value;

    public LongCalculate(long value) {
        this.value = value;
    }

    @Override
    public int parse(String str, int index) {
        return -1;
    }

    @Override
    public Class<?> getReturnClass() {
        return Long.class;
    }
}
