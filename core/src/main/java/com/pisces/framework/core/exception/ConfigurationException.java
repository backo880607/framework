package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 配置异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class ConfigurationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3824627521737787544L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
