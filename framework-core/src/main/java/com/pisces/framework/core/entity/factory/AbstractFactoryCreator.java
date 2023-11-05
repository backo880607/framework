package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BeanObject;

import java.lang.reflect.Field;

/**
 * 抽象工厂创造者
 *
 * @author jason
 * @date 2023/07/19
 */
public abstract class AbstractFactoryCreator {
    protected AbstractFactoryCreator() {
        FactoryManager.registerCreator(this);
    }

    public abstract AbstractFactory create(Class<? extends BeanObject> beanClass);

    public void init() {
    }

    public boolean checkProperty(Class<? extends BeanObject> beanClass, Field field) throws Exception {
        return false;
    }
}
