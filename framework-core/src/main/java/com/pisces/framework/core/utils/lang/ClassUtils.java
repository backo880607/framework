package com.pisces.framework.core.utils.lang;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.type.Duration;
import com.pisces.framework.type.MultiEnum;
import com.pisces.framework.type.PROPERTY_TYPE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

/**
 * 类跑龙套
 *
 * @author jason
 * @date 2023/06/23
 */
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
        } else if (BeanObject.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.BEAN;
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
}
