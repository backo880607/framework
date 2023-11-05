package com.pisces.framework.core.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 表达异常
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class ExpressionException extends BaseException {
    @Serial
    private static final long serialVersionUID = -8094703802819527529L;
    private Integer begin;
    private Integer end;

    public ExpressionException(int begin, int end, Enum<?> key, Object... args) {
        super(key, args);
        this.begin = begin;
        this.end = end;
    }
}
