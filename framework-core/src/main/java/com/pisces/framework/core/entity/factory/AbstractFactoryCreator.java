package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BeanObject;

public abstract class AbstractFactoryCreator {
    protected AbstractFactoryCreator() {
        FactoryManager.registerCreator(this);
    }
    public abstract AbstractFactory create(Class<? extends BeanObject> beanClass);
    public void init() {}
}
