package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 表达异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class ExpressionException extends BaseException {
    @Serial
    private static final long serialVersionUID = -8094703802819527529L;

    public ExpressionException(Enum<?> key, Object... args) {
        super(key, args);
    }
}
