package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.query.expression.OperationType;

import java.util.function.BiFunction;

/**
 * ③类型计算
 *
 * @author jason
 * @date 2022/12/07
 */
public class OperTypeCalculate implements Calculate {
    OperationType type;
    BiFunction<Class<?>, Class<?>, Class<?>> returnClass;

    public OperTypeCalculate(OperationType type) {
        this.type = type;
    }

    @Override
    public int parse(String str, int index) {
        return -1;
    }

    @Override
    public Class<?> getReturnClass() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getReturnClass(Class<?> lClass, Class<?> rClass) {
        return returnClass.apply(lClass, rClass);
    }
}
