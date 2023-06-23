package com.pisces.framework.core.service;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.factory.FactoryManager;
import com.pisces.framework.core.exception.SystemException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务经理
 *
 * @author jason
 * @date 2022/12/07
 */
public class ServiceManager {
    private static final Map<Class<? extends BeanObject>, BeanService<? extends BeanObject>> SERVICES = new HashMap<>();

    protected ServiceManager() {
    }

    public static Set<Class<? extends BeanObject>> getBeanClasses() {
        return SERVICES.keySet();
    }

    public static void register(Class<? extends BeanObject> beanClass, BeanService<? extends BeanObject> service) {
        if (SERVICES.containsKey(beanClass)) {
            throw new SystemException(beanClass.getName() + " has registered!");
        }
        SERVICES.put(beanClass, service);
    }

    public static <T extends BeanObject> BeanService<T> getService(Class<T> beanClass) {
        return (BeanService<T>) SERVICES.get(beanClass);
    }

    public static <T extends BeanObject> BeanService<T> fetchService(Class<T> beanClass) {
        if (beanClass == null) {
            throw new SystemException("class is null");
        }
        BeanService<T> service = getService(beanClass);
        if (service == null) {
            throw new SystemException(beanClass.getName() + " has not bind a service!");
        }
        return service;
    }
}
