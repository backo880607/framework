package com.pisces.framework.web.controller;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.core.service.BaseService;
import com.pisces.framework.core.service.BeanService;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制器经理
 *
 * @author jason
 * @date 2022/12/08
 */
public class ControllerManager {

    private static final Map<Class<? extends BeanObject>, BeanController<? extends BeanObject, ? extends BeanService<? extends BeanObject>>> CONTROLLERS = new HashMap<>();

    public static void register(Class<? extends BeanObject> beanClass, BeanController<? extends BeanObject, ? extends BeanService<? extends BeanObject>> controller) {
        if (CONTROLLERS.containsKey(beanClass)) {
            throw new SystemException(beanClass.getName() + " has registered!");
        }
        CONTROLLERS.put(beanClass, controller);
    }

    public static <T extends BeanObject> BeanController<T, ? extends BeanService<T>> getController(Class<T> beanClass) {
        return (BeanController<T, ? extends BeanService<T>>) CONTROLLERS.get(beanClass);
    }

    public static <T extends BeanObject> BeanController<T, ? extends BeanService<T>> fetchController(Class<T> beanClass) {
        if (beanClass == null) {
            throw new SystemException("class is null");
        }
        BeanController<T, ? extends BeanService<T>> service = getController(beanClass);
        if (service == null) {
            throw new SystemException(beanClass.getName() + " has not bind a service!");
        }
        return service;
    }

    public static <T extends BeanObject> String getPrefixUrl(Class<T> entityClass) {
        RequestMapping mapping = ControllerManager.fetchController(entityClass).getClass().getAnnotation(RequestMapping.class);
        if (mapping == null || mapping.value().length == 0) {
            return "";
        }
        String url = mapping.value()[0];
        int lastIndex = url.lastIndexOf('/');
        if (lastIndex <= 0) {
            return "";
        }

        return url.substring(url.startsWith("/") ? 1 : 0, lastIndex);
    }
}
