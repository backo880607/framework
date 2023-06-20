package com.pisces.framework.rds.exception;

import com.pisces.framework.core.exception.BaseException;

/**
 * rds例外
 *
 * @author jason
 * @date 2022/12/07
 */
public class RdsException extends BaseException {
    public RdsException(Enum<?> enumKey, Object... argements) {
        super(enumKey, argements);
    }
}
