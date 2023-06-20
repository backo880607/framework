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
public class BeanFactory {
    private final Class<? extends BeanObject> beanClass;
    String identify = "";
    BeanFactory superFactory = null;
    List<BeanFactory> childFactories = new ArrayList<>();

    public BeanFactory(Class<? extends BeanObject> value) {
        this.beanClass = value;
    }

    public <T extends BeanObject> Class<T> getBeanClass() {
        return (Class<T>) this.beanClass;
    }

    public BeanFactory getSuperFactory() {
        return this.superFactory;
    }

    public List<BeanFactory> getChildFactories() {
        return this.childFactories;
    }
}
