package com.pisces.framework.core.exception;

import com.pisces.framework.core.entity.Property;

import java.io.Serial;

/**
 * 属性异常
 *
 * @author jason
 * @date 2022/12/07
 */
public class PropertyException extends BaseException {
    @Serial
    private static final long serialVersionUID = 8145333368099939834L;
    private Property property;

    public PropertyException(Enum<?> key, Object... args) {
        super(key, args);
    }


    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}
