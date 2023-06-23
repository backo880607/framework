package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BeanObject;

public class BeanFactory extends AbstractFactory {
    public BeanFactory(Class<? extends BeanObject> value) {
        super(value);
    }
}
