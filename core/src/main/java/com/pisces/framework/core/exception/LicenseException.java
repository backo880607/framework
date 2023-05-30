package com.pisces.framework.core.exception;

import java.io.Serial;

/**
 * 许可例外
 *
 * @author jason
 * @date 2022/12/07
 */
public class LicenseException extends BaseException {
    @Serial
    private static final long serialVersionUID = 2982990613157147413L;

    public LicenseException(Enum<?> key, Object... args) {
        super(key, args);
    }
}
