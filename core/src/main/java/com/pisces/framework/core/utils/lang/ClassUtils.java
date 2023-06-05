package com.pisces.framework.core.utils.lang;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.entity.Duration;
import com.pisces.framework.core.entity.MultiEnum;
import com.pisces.framework.core.enums.PROPERTY_TYPE;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;

public class ClassUtils {

    public static PROPERTY_TYPE getPropertyType(Class<?> clazz) {
        PROPERTY_TYPE type;
        if (clazz == Boolean.class || clazz == boolean.class) {
            type = PROPERTY_TYPE.BOOLEAN;
        } else if (clazz == Short.class || clazz == short.class) {
            type = PROPERTY_TYPE.SHORT;
        } else if (clazz == Integer.class || clazz == int.class) {
            type = PROPERTY_TYPE.INTEGER;
        } else if (clazz == Long.class || clazz == long.class) {
            type = PROPERTY_TYPE.LONG;
        } else if (clazz == Double.class || clazz == double.class) {
            type = PROPERTY_TYPE.DOUBLE;
        } else if (clazz == String.class) {
            type = PROPERTY_TYPE.STRING;
        } else if (clazz == Date.class) {
            type = PROPERTY_TYPE.DATE_TIME;
        } else if (clazz == Duration.class) {
            type = PROPERTY_TYPE.DURATION;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.ENUM;
        } else if (MultiEnum.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.MULTI_ENUM;
        } else if (BaseObject.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.ENTITY;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.LIST;
        } else {
            throw new UnsupportedOperationException("not support type: " + clazz.getName());
        }
        return type;
    }

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
