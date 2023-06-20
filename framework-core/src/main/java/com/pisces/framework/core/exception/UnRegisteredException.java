package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 联合国注册异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class UnRegisteredException extends BaseException {
    @Serial
    private static final long serialVersionUID = -5838160633388320158L;

    public UnRegisteredException(Enum<?> key, Object... args) {
        super(key, args);
    }
}
