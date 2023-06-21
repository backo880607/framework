package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BeanObject;

public interface FactoryCreator {
    BeanFactory create(Class<? extends BeanObject> beanClass);
}
