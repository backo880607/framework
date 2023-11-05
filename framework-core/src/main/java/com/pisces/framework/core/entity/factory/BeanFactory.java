package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.type.PROPERTY_TYPE;

import java.lang.reflect.InvocationTargetException;

/**
 * bean工厂对象
 *
 * @author jason
 * @date 2023/07/19
 */
public class BeanFactory extends AbstractFactory {
    public BeanFactory(Class<? extends BeanObject> value) {
        super(value);
    }

    @Override
    public Object getValue(BeanObject bean, Property property) {
        if (bean == null || property == null || property.getGetMethod() == null) {
            return null;
        }

        Object value = null;
        try {
            value = property.getGetMethod().invoke(bean);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
        }

        return value;
    }

    @Override
    public void setValue(BeanObject bean, Property property, Object value) {
        if (bean == null || property == null || property.getSetMethod() == null) {
            return;
        } else if (value == null && property.getType() != PROPERTY_TYPE.BEAN) {
            return;
        }

        try {
            property.getSetMethod().invoke(bean, value);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }
}
