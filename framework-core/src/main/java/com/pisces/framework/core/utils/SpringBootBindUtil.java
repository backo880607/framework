package com.pisces.framework.core.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class SpringBootBindUtil {
    public static final IBind BIND;

    public SpringBootBindUtil() {
    }

    public static <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
        return BIND.bind(environment, targetClass, prefix);
    }

    static {
        IBind bind;
        try {
            Class.forName("org.springframework.boot.context.properties.bind.Binder");
            bind = new SpringBoot2Bind();
        } catch (Exception var2) {
            bind = new SpringBoot1Bind();
        }

        BIND = bind;
    }

    public static class SpringBoot2Bind implements IBind {
        public SpringBoot2Bind() {
        }

        @Override
        public <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
            try {
                Class<?> bindClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
                Method getMethod = bindClass.getDeclaredMethod("get", Environment.class);
                Method bindMethod = bindClass.getDeclaredMethod("bind", String.class, Class.class);
                Object bind = getMethod.invoke(null, environment);
                Object bindResult = bindMethod.invoke(bind, prefix, targetClass);
                Method resultGetMethod = bindResult.getClass().getDeclaredMethod("get");
                Method isBoundMethod = bindResult.getClass().getDeclaredMethod("isBound");
                return (Boolean) isBoundMethod.invoke(bindResult) ? (T) resultGetMethod.invoke(bindResult) : null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static class SpringBoot1Bind implements IBind {
        public SpringBoot1Bind() {
        }

        @Override
        public <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
            try {
                Class<?> resolverClass = Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
                Constructor<?> resolverConstructor = resolverClass.getDeclaredConstructor(PropertyResolver.class);
                Method getSubPropertiesMethod = resolverClass.getDeclaredMethod("getSubProperties", String.class);
                Object resolver = resolverConstructor.newInstance(environment);
                Map<String, Object> properties = (Map) getSubPropertiesMethod.invoke(resolver, "");
                T target = BeanUtils.instantiateClass(targetClass);
                Class<?> binderClass = Class.forName("org.springframework.boot.bind.RelaxedDataBinder");
                Constructor<?> binderConstructor = binderClass.getDeclaredConstructor(Object.class, String.class);
                Method bindMethod = binderClass.getMethod("bind", PropertyValues.class);
                Object binder = binderConstructor.newInstance(target, prefix);
                bindMethod.invoke(binder, new MutablePropertyValues(properties));
                return target;
            } catch (Exception var14) {
                throw new RuntimeException(var14);
            }
        }
    }

    public interface IBind {
        <T> T bind(Environment var1, Class<T> var2, String var3);
    }
}
