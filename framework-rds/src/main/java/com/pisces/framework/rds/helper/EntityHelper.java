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
import com.pisces.framework.rds.helper.entity.Config;
import com.pisces.framework.rds.helper.entity.EntityColumn;
import com.pisces.framework.rds.helper.entity.EntityTable;
import com.pisces.framework.rds.provider.resolve.DefaultEntityResolve;
import com.pisces.framework.rds.provider.resolve.EntityResolve;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类工具类 - 处理实体和数据库表以及字段关键的一个类
 * <p/>
 * <p>项目地址 : <a href="https://github.com/abel533/Mapper" target="_blank">https://github.com/abel533/Mapper</a></p>
 *
 * @author liuzh
 */
public class EntityHelper {

    /**
     * 实体类 => 表对象
     */
    private static final Map<Class<?>, EntityTable> ENTITY_TABLES = new ConcurrentHashMap<>();

    /**
     * 实体类解析器
     */
    private static EntityResolve resolve = new DefaultEntityResolve();

    /**
     * 获取表对象
     *
     * @param beanClass 实体类
     * @return {@link EntityTable}
     */
    public static EntityTable getEntityTable(Class<?> beanClass) {
        EntityTable entityTable = ENTITY_TABLES.get(beanClass);
        if (entityTable == null) {
            throw new SystemException("无法获取实体类" + beanClass.getName() + "对应的表名!");
        }
        return entityTable;
    }

    /**
     * 获取全部列
     *
     * @param beanClass 实体类
     * @return {@link Set}<{@link EntityColumn}>
     */
    public static Set<EntityColumn> getColumns(Class<?> beanClass) {
        
        return getEntityTable(beanClass).getBeanColumns();
    }

    /**
     * 获取主键信息
     *
     * @param beanClass 实体类
     * @return {@link Set}<{@link EntityColumn}>
     */
    public static Set<EntityColumn> getPkColumns(Class<?> beanClass) {
        return getEntityTable(beanClass).getBeanPKColumns();
    }

    /**
     * 获取查询的Select
     *
     * @param beanClass 实体类
     * @return {@link String}
     */
    public static String getSelectColumns(Class<?> beanClass) {
        EntityTable entityTable = getEntityTable(beanClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }
        Set<EntityColumn> columnList = getColumns(beanClass);
        StringBuilder selectBuilder = new StringBuilder();
        boolean skipAlias = Map.class.isAssignableFrom(beanClass);
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn());
            if (!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())) {
                //不等的时候分几种情况，例如`DESC`
                if (entityColumn.getColumn().substring(1, entityColumn.getColumn().length() - 1).equalsIgnoreCase(entityColumn.getProperty())) {
                    selectBuilder.append(",");
                } else {
                    selectBuilder.append(" AS ").append(entityColumn.getProperty()).append(",");
                }
            } else {
                selectBuilder.append(",");
            }
        }
        entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        return entityTable.getBaseSelect();
    }

    /**
     * 初始化实体属性
     *
     * @param beanClass 实体类
     * @param config    配置
     */
    public static synchronized void initEntityNameMap(Class<?> beanClass, Config config) {
        if (ENTITY_TABLES.get(beanClass) != null) {
            return;
        }
        //创建并缓存EntityTable
        EntityTable entityTable = resolve.resolveEntity(beanClass, config);
        ENTITY_TABLES.put(beanClass, entityTable);
    }

    /**
     * 设置实体类解析器
     *
     * @param resolve 实体类解析器
     */
    static void setResolve(EntityResolve resolve) {
        EntityHelper.resolve = resolve;
    }
}