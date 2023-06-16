package com.pisces.framework.core.service;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.entity.factory.FactoryManager;
import com.pisces.framework.core.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务经理
 *
 * @author jason
 * @date 2022/12/07
 */
public class ServiceManager {
    private static final Map<Class<? extends BaseObject>, BaseService<? extends BaseObject>> SERVICES = new HashMap<>();

    protected ServiceManager() {
    }

    public static void register(Class<? extends BaseObject> entityClass, BaseService<? extends BaseObject> service) {
        if (SERVICES.containsKey(entityClass)) {
            throw new SystemException(entityClass.getName() + " has registered!");
        }
        SERVICES.put(entityClass, service);
        FactoryManager.registerEntityClass(entityClass);
    }

    public static <T extends BaseObject> BaseService<T> getService(Class<T> entityClass) {
        return (BaseService<T>) SERVICES.get(entityClass);
    }

    public static <T extends BaseObject> BaseService<T> fetchService(Class<T> entityClass) {
        if (entityClass == null) {
            throw new SystemException("class is null");
        }
        BaseService<T> service = getService(entityClass);
        if (service == null) {
            throw new SystemException(entityClass.getName() + " has not bind a service!");
        }
        return service;
    }
}
