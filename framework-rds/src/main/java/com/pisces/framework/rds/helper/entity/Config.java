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

package com.pisces.framework.rds.helper.entity;

import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.provider.resolve.EntityResolve;
import com.pisces.framework.rds.utils.SimpleTypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 通用Mapper属性配置
 *
 * @author liuzh
 */
public class Config {
    public static final String PREFIX = "mapper";

    private List<Class<?>> mappers = new ArrayList<>();
    private String catalog;
    private String schema;
    /**
     * 对于一般的getAllIfColumnNode，是否判断!=''，默认不判断
     */
    private boolean notEmpty;
    /**
     * 处理关键字，默认空，mysql可以设置为 `{0}`, sqlserver 为 [{0}]，{0} 代表的列名
     */
    private String wrapKeyword = "";
    /**
     * 配置解析器
     */
    private Class<? extends EntityResolve> resolveClass;
    /**
     * 安全删除，开启后，不允许删全表，如 delete from table
     */
    private boolean safeDelete;
    /**
     * 安全更新，开启后，不允许更新全表，如 update table set xx=?
     */
    private boolean safeUpdate;

    public String getCatalog() {
        return catalog;
    }

    /**
     * 设置全局的catalog,默认为空，如果设置了值，操作表时的sql会是catalog.tablename
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * 获取表前缀，带catalog或schema
     */
    public String getPrefix() {
        if (StringUtils.isNotEmpty(this.catalog)) {
            return this.catalog;
        }
        if (StringUtils.isNotEmpty(this.schema)) {
            return this.schema;
        }
        return "";
    }

    public String getSchema() {
        return schema;
    }

    /**
     * 设置全局的schema,默认为空，如果设置了值，操作表时的sql会是schema.tablename
     * <br>如果同时设置了catalog,优先使用catalog.tablename
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getWrapKeyword() {
        return wrapKeyword;
    }

    public void setWrapKeyword(String wrapKeyword) {
        this.wrapKeyword = wrapKeyword;
    }

    public boolean isNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty;
    }

    public List<Class<?>> getMappers() {
        return mappers;
    }

    public void setMappers(List<Class<?>> mappers) {
        this.mappers = mappers;
    }

    public Class<? extends EntityResolve> getResolveClass() {
        return resolveClass;
    }

    public void setResolveClass(Class<? extends EntityResolve> resolveClass) {
        this.resolveClass = resolveClass;
    }

    public boolean isSafeDelete() {
        return safeDelete;
    }

    public void setSafeDelete(boolean safeDelete) {
        this.safeDelete = safeDelete;
    }

    public boolean isSafeUpdate() {
        return safeUpdate;
    }

    public void setSafeUpdate(boolean safeUpdate) {
        this.safeUpdate = safeUpdate;
    }

    /**
     * 配置属性
     *
     * @param properties 属性
     */
    public void setProperties(Properties properties) {
        if (properties == null) {
            return;
        }
        String catalog = properties.getProperty("catalog");
        if (StringUtils.isNotEmpty(catalog)) {
            setCatalog(catalog);
        }
        String schema = properties.getProperty("schema");
        if (StringUtils.isNotEmpty(schema)) {
            setSchema(schema);
        }

        this.notEmpty = Boolean.parseBoolean(properties.getProperty("notEmpty"));
        //注册新的基本类型，以逗号隔开，使用全限定类名
        String simpleTypes = properties.getProperty("simpleTypes");
        if (StringUtils.isNotEmpty(simpleTypes)) {
            SimpleTypeUtil.registerSimpleType(simpleTypes);
        }
        //使用 8 种基本类型
        if (Boolean.parseBoolean(properties.getProperty("usePrimitiveType"))) {
            SimpleTypeUtil.registerPrimitiveTypes();
        }
        //处理关键字
        String wrapKeyword = properties.getProperty("wrapKeyword");
        if (StringUtils.isNotEmpty(wrapKeyword)) {
            this.wrapKeyword = wrapKeyword;
        }
        //安全删除
        this.safeDelete = Boolean.parseBoolean(properties.getProperty("safeDelete"));
        //安全更新
        this.safeUpdate = Boolean.parseBoolean(properties.getProperty("safeUpdate"));
    }
}
