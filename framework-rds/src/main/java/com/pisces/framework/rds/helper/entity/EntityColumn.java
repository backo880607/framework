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
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.Objects;

/**
 * 数据库表对应的列
 *
 * @author liuzh
 */
public class EntityColumn {
    private EntityTable table;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private Class<? extends TypeHandler<?>> typeHandler;
    private boolean id = false;
    private boolean identity = false;
    //字段是否为 blob
    private boolean blob;
    //可插入
    private boolean insertable = true;
    //可更新
    private boolean updatable = true;
    /**
     * 对应的字段信息
     *
     * @since 3.5.0
     */
    private EntityField entityField;

    public EntityColumn() {
    }

    public EntityColumn(EntityTable table) {
        this.table = table;
    }

    /**
     * 返回格式如:colum = #{entityName.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName 实体名称
     * @return {@link String}
     */
    public String getColumnEqualsHolder(String entityName) {
        return this.column + " = " + getColumnHolder(entityName);
    }

    /**
     * 返回格式如:#{entityName.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName 实体名称
     * @return {@link String}
     */
    public String getColumnHolder(String entityName) {
        return getColumnHolder(entityName, null);
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName 实体名称
     * @param suffix     后缀
     * @return {@link String}
     */
    public String getColumnHolder(String entityName, String suffix) {
        return getColumnHolder(entityName, null, null);
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler},
     *
     * @param entityName 实体名称
     * @param suffix     后缀
     * @return {@link String}
     */
    public String getColumnHolderWithComma(String entityName, String suffix) {
        return getColumnHolder(entityName, suffix, ",");
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
     *
     * @param entityName 实体名称
     * @param suffix     后缀
     * @param separator  分隔符
     * @return {@link String}
     */
    public String getColumnHolder(String entityName, String suffix, String separator) {
        StringBuffer sb = new StringBuffer("#{");
        if (StringUtils.isNotEmpty(entityName)) {
            sb.append(entityName);
            sb.append(".");
        }
        sb.append(this.property);
        if (StringUtils.isNotEmpty(suffix)) {
            sb.append(suffix);
        }
        //如果 null 被当作值来传递，对于所有可能为空的列，JDBC Type 是需要的
        if (this.jdbcType != null) {
            sb.append(", jdbcType=");
            sb.append(this.jdbcType.toString());
        }
        //为了以后定制类型处理方式，你也可以指定一个特殊的类型处理器类，例如枚举
        if (this.typeHandler != null) {
            sb.append(", typeHandler=");
            sb.append(this.typeHandler.getName());
        }
        sb.append("}");
        if (StringUtils.isNotEmpty(separator)) {
            sb.append(separator);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EntityColumn that = (EntityColumn) o;

        if (id != that.id) {
            return false;
        }
        if (identity != that.identity) {
            return false;
        }
        if (!Objects.equals(table, that.table)) {
            return false;
        }
        if (!Objects.equals(property, that.property)) {
            return false;
        }
        if (!Objects.equals(column, that.column)) {
            return false;
        }
        if (!Objects.equals(javaType, that.javaType)) {
            return false;
        }
        if (jdbcType != that.jdbcType) {
            return false;
        }
        return Objects.equals(typeHandler, that.typeHandler);
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        result = 31 * result + (jdbcType != null ? jdbcType.hashCode() : 0);
        result = 31 * result + (typeHandler != null ? typeHandler.hashCode() : 0);
        result = 31 * result + (id ? 1 : 0);
        result = 31 * result + (identity ? 1 : 0);
        return result;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return {@link String}
     */
    public String getColumnEqualsHolder() {
        return getColumnEqualsHolder(null);
    }

    /**
     * 返回格式如:#{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return {@link String}
     */
    public String getColumnHolder() {
        return getColumnHolder(null);
    }

    public EntityField getEntityField() {
        return entityField;
    }

    public void setEntityField(EntityField entityField) {
        this.entityField = entityField;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public EntityTable getTable() {
        return table;
    }

    public void setTable(EntityTable table) {
        this.table = table;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isIdentity() {
        return identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isBlob() {
        return blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
    }

    @Override
    public String toString() {
        return "EntityColumn{" +
                "table=" + table.getName() +
                ", property='" + property + '\'' +
                ", column='" + column + '\'' +
                ", javaType=" + javaType +
                ", jdbcType=" + jdbcType +
                ", typeHandler=" + typeHandler +
                ", id=" + id +
                ", identity=" + identity +
                ", blob=" + blob +
                ", insertable=" + insertable +
                ", updatable=" + updatable +
                '}';
    }
}
