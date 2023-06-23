package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BeanObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体工厂
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class AbstractFactory {
    private final Class<? extends BeanObject> beanClass;
    String identify = "";
    AbstractFactory superFactory = null;
    List<AbstractFactory> childFactories = new ArrayList<>();

    public AbstractFactory(Class<? extends BeanObject> value) {
        this.beanClass = value;
    }

    public Class<? extends BeanObject> getBeanClass() {
        return this.beanClass;
    }

    public AbstractFactory getSuperFactory() {
        return this.superFactory;
    }

    public List<AbstractFactory> getChildFactories() {
        return this.childFactories;
    }
}
