package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.exception.ExpressionException;

/**
 * 计算
 *
 * @author jason
 * @date 2022/12/07
 */
public interface Calculate {

    /**
     * 解析
     *
     * @param str   str
     * @param index 指数
     * @return int
     */
    int parse(String str, int index) throws ExpressionException;

    /**
     * 得到返回类
     *
     * @return {@link Class}<{@link ?}>
     */
    Class<?> getReturnClass();
}
