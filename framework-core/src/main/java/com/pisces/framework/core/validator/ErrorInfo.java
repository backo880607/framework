package com.pisces.framework.core.validator;

import com.pisces.framework.core.entity.BeanObject;

/**
 * 错误信息
 *
 * @author jason
 * @date 2022/12/08
 */
public class ErrorInfo {
    private String objectClass;
    private String property;
    private String message;
    private String value;
    private BeanObject object;

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String value) {
        this.objectClass = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String value) {
        this.property = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BeanObject getObject() {
        return object;
    }

    public void setObject(BeanObject object) {
        this.object = object;
    }
}
