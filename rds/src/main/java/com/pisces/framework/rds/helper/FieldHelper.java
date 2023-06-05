/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.pisces.framework.rds.helper;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.rds.entity.EntityField;
import jakarta.persistence.Entity;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * 类字段工具类
 *
 * @author liuzh
 * @since 2015-12-06 18:38
 */
public class FieldHelper {

    /**
     * 获取全部的Field
     *
     * @param entityClass 实体类
     * @return {@link List}<{@link EntityField}>
     */
    public static List<EntityField> getFields(Class<?> entityClass) {
        List<EntityField> fields = _getFields(entityClass, null, null);
        fields = new ArrayList<>(new LinkedHashSet<>(fields));
        List<EntityField> properties = getProperties(entityClass);
        for (EntityField field : fields) {
            for (EntityField property : properties) {
                if (field.getName().equals(property.getName())) {
                    //泛型的情况下通过属性可以得到实际的类型
                    field.setJavaType(property.getJavaType());
                    break;
                }
            }
        }
        return fields;
    }

    /**
     * 获取全部的属性，通过方法名获取
     *
     * @param entityClass 实体类
     * @return {@link List}<{@link EntityField}>
     */
    public static List<EntityField> getProperties(Class<?> entityClass) {
        List<EntityField> entityFields = new ArrayList<>();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (IntrospectionException e) {
            throw new SystemException(e);
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor desc : descriptors) {
            if (!"class".equals(desc.getName())) {
                entityFields.add(new EntityField(null, desc));
            }
        }
        return entityFields;
    }

    /**
     * 获取全部的Field，仅仅通过Field获取
     *
     * @param entityClass 实体类
     * @param fieldList   字段列表
     * @param level       水平
     * @return {@link List}<{@link EntityField}>
     */
    private static List<EntityField> _getFields(Class<?> entityClass, List<EntityField> fieldList, Integer level) {
        if (fieldList == null) {
            fieldList = new ArrayList<>();
        }
        if (level == null) {
            level = 0;
        }
        if (entityClass.equals(Object.class)) {
            return fieldList;
        }
        Field[] fields = entityClass.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            //排除静态字段，解决bug#2
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                //如果父类中包含与子类同名field，则跳过处理，允许子类进行覆盖
                if (FieldHelper.containFiled(fieldList, field.getName())) {
                    continue;
                }
                if (level != 0) {
                    //将父类的字段放在前面
                    fieldList.add(index, new EntityField(field, null));
                    index++;
                } else {
                    fieldList.add(new EntityField(field, null));
                }
            }
        }
        Class<?> superClass = entityClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && (superClass.isAnnotationPresent(Entity.class)
                || (!Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)))) {
            return _getFields(entityClass.getSuperclass(), fieldList, ++level);
        }
        return fieldList;
    }


    /**
     * 判断是否已经包含同名的field
     *
     * @param fieldList 字段列表
     * @param filedName 提起名字
     * @return boolean
     */
    private static boolean containFiled(List<EntityField> fieldList, String filedName) {
        for (EntityField field : fieldList) {
            if (field.getName().equals(filedName)) {
                return true;
            }
        }
        return false;
    }
}
