package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 注册异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class RegisteredException extends BaseException {
    @Serial
    private static final long serialVersionUID = -2629496305888046864L;

    public RegisteredException(Enum<?> key, Object... args) {
        super(key, args);
    }
}
