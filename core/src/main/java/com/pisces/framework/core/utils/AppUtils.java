package com.pisces.framework.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 应用跑龙套
 *
 * @author jason
 * @date 2022/12/07
 */
public final class AppUtils {
    private static ApplicationContext context;

    private AppUtils() {
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        AppUtils.context = context;
    }

    public static String getProperty(String name) {
        return context.getEnvironment().getProperty(name);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return context.getBean(requiredType);
    }

    public static <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }
}
