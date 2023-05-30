package com.pisces.framework.core.utils.lang;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ClassUtils {

    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray()
                || clazz == int[].class
                || clazz == long[].class
                || clazz == short[].class
                || clazz == float[].class
                || clazz == double[].class;
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<?> defaultConstructor = null;
            Constructor<?> otherConstructor = null;

            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : declaredConstructors) {
                if (constructor.getParameterCount() == 0) {
                    defaultConstructor = constructor;
                } else if (Modifier.isPublic(constructor.getModifiers())) {
                    otherConstructor = constructor;
                }
            }
            if (defaultConstructor != null) {
                return (T) defaultConstructor.newInstance();
            } else if (otherConstructor != null) {
                Class<?>[] parameterTypes = otherConstructor.getParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i].isPrimitive()) {
                        parameters[i] = ConvertUtils.getPrimitiveDefaultValue(parameterTypes[i]);
                    } else {
                        parameters[i] = null;
                    }
                }
                return (T) otherConstructor.newInstance(parameters);
            }
            throw new IllegalArgumentException("the class \"" + clazz.getName() + "\" has no constructor.");
        } catch (Exception e) {
            throw new RuntimeException("Can not newInstance class: " + clazz.getName());
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... paras) {
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (isMatchedParas(constructor, paras)) {
                    Object ret = constructor.newInstance(paras);
                    return (T) ret;
                }
            }
            throw new IllegalArgumentException("Can not find constructor by paras: \"" + Arrays.toString(paras) + "\" in class[" + clazz.getName() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isMatchedParas(Constructor<?> constructor, Object[] paras) {
        if (constructor.getParameterCount() == 0) {
            return paras == null || paras.length == 0;
        }

        if (constructor.getParameterCount() > 0
                && (paras == null || paras.length != constructor.getParameterCount())) {
            return false;
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object paraObject = paras[i];
            if (paraObject != null && !parameterType.isAssignableFrom(paraObject.getClass())) {
                return false;
            }
        }

        return true;
    }

    public static List<Field> getAllFields(Class<?> cl) {
        List<Field> fields = new ArrayList<>();
        doGetFields(cl, fields, null);
        return fields;
    }

    public static List<Field> getAllFields(Class<?> cl, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        doGetFields(cl, fields, predicate);
        return fields;
    }

    private static void doGetFields(Class<?> cl, List<Field> fields, Predicate<Field> predicate) {
        if (cl == null || cl == Object.class) {
            return;
        }

        Field[] declaredFields = cl.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (predicate == null || predicate.test(declaredField)) {
                fields.add(declaredField);
            }
        }

        doGetFields(cl.getSuperclass(), fields, predicate);
    }

    public static List<Method> getAllMethods(Class<?> cl) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(cl, methods, null);
        return methods;
    }

    public static List<Method> getAllMethods(Class<?> cl, Predicate<Method> predicate) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(cl, methods, predicate);
        return methods;
    }


    private static void doGetMethods(Class<?> cl, List<Method> methods, Predicate<Method> predicate) {
        if (cl == null || cl == Object.class) {
            return;
        }

        Method[] declaredMethods = cl.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (predicate == null || predicate.test(method)) {
                methods.add(method);
            }
        }

        doGetMethods(cl.getSuperclass(), methods, predicate);
    }

    private static <T> Class<T> getJdkProxySuperClass(Class<T> clazz) {
        final Class<?> proxyClass = Proxy.getProxyClass(clazz.getClassLoader(), clazz.getInterfaces());
        return (Class<T>) proxyClass.getInterfaces()[0];
    }
}
