package com.pisces.framework.core.exception;

import java.text.MessageFormat;

/**
 * 验证异常
 *
 * @author Jason
 * @date 2022-02-25
 */
public class ValidateException extends RuntimeException {
    public ValidateException() {
    }

    public ValidateException(String msg) {
        super(msg);
    }

    public ValidateException(String messageTemplate, Object... params) {
        super(MessageFormat.format(messageTemplate, params));
    }

    public ValidateException(Throwable throwable) {
        super(throwable);
    }

    public ValidateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ValidateException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }
}
