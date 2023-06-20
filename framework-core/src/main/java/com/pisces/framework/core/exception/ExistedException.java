package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 存在异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class ExistedException extends BaseException {
    @Serial
    private static final long serialVersionUID = -8682468377397594960L;

    public ExistedException(Enum<?> key, Object... args) {
        super(key, args);
    }
}
