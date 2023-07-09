package com.pisces.framework.core.exception;

public class CommonException extends BaseException {

    public CommonException(Enum<?> key, Object... args) {
        super(key, args);
    }
}
