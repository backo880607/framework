package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 系统异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class SystemException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4078215306662957769L;

    public SystemException() {
        super();
    }

    public SystemException(String msg) {
        super(msg);
    }

    public SystemException(String msg, Object... params) {
        super(String.format(msg, params));
    }

    public SystemException(Throwable cause, String msg) {
        super(msg, cause);
    }

    public SystemException(Throwable cause, String msg, Object... params) {
        super(String.format(msg, params), cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
