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
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.annotation.LogicDelete;
import com.pisces.framework.rds.annotation.Version;
import com.pisces.framework.rds.helper.entity.EntityColumn;

import java.util.Set;

/**
 * 拼常用SQL的工具类
 *
 * @author liuzh
 * @since 2015-11-03 22:40
 */
public class SqlHelper {

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     * @return {@link String}
     */
    public static String getBindCache(EntityColumn column) {
        return "<bind name=\"" + column.getProperty() + "_cache\" " + "value=\"" + column.getProperty() + "\"/>";
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     * @param value  价值
     * @return {@link String}
     */
    public static String getBindValue(EntityColumn column, String value) {
        return "<bind name=\"" + column.getProperty() + "_bind\" " + "value='" + value + "'/>";
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column   列
     * @param contents 内容
     * @return {@link String}
     */
    public static String getIfCacheNotNull(EntityColumn column, String contents) {
        return "<if test=\"" + column.getProperty() + "_cache != null\">" + contents + "</if>";
    }

    /**
     * 如果_cache == null
     *
     * @param column   列
     * @param contents 内容
     * @return {@link String}
     */
    public static String getIfCacheIsNull(EntityColumn column, String contents) {
        return "<if test=\"" + column.getProperty() + "_cache == null\">" + contents + "</if>";
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column   列
     * @param contents 内容
     * @param empty    空
     * @return {@link String}
     */
    public static String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        return getIfNotNull(null, column, contents, empty);
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param column   列
     * @param contents 内容
     * @param empty    空
     * @return {@link String}
     */
    public static String getIfIsNull(EntityColumn column, String contents, boolean empty) {
        return getIfIsNull(null, column, contents, empty);
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param entityName 实体名称
     * @param column     列
     * @param contents   内容
     * @param empty      空
     * @return {@link String}
     */
    public static String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (StringUtils.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            if (StringUtils.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param entityName 实体名称
     * @param column     列
     * @param contents   内容
     * @param empty      空
     * @return {@link String}
     */
    public static String getIfIsNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (StringUtils.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" == null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" or ");
            if (StringUtils.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" == '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnSet) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * select xxx,xxx...
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String selectAllColumns(Class<?> entityClass) {
        return "SELECT " + getAllColumns(entityClass) + " ";
    }

    /**
     * select count(x)
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String selectCount(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        return sql.toString();
    }

    /**
     * select case when count(x) > 0 then 1 else 0 end
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String selectCountExists(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CASE WHEN ");
        Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        sql.append(" > 0 THEN 1 ELSE 0 END AS result ");
        return sql.toString();
    }

    /**
     * from tableName - 动态表名
     *
     * @param entityClass      实体类
     * @param defaultTableName 默认表名
     * @return {@link String}
     */
    public static String fromTable(Class<?> entityClass, String defaultTableName) {
        return " FROM " + defaultTableName + " ";
    }

    /**
     * update tableName - 动态表名
     *
     * @param entityClass      实体类
     * @param defaultTableName 默认表名
     * @return {@link String}
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName) {
        return updateTable(entityClass, defaultTableName, null);
    }

    /**
     * update tableName - 动态表名
     *
     * @param entityClass      实体类
     * @param defaultTableName 默认表名
     * @param entityName       实体名称
     * @return {@link String}
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName, String entityName) {
        return "UPDATE " + defaultTableName + " ";
    }

    /**
     * delete tableName - 动态表名
     *
     * @param entityClass      实体类
     * @param defaultTableName 默认表名
     * @return {@link String}
     */
    public static String deleteFromTable(Class<?> entityClass, String defaultTableName) {
        return "DELETE FROM " + defaultTableName + " ";
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass      实体类
     * @param defaultTableName 默认表名
     * @return {@link String}
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName) {
        return "INSERT INTO " + defaultTableName + " ";
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass      实体类
     * @param defaultTableName 默认表名
     * @param parameterName    参数名称
     * @return {@link String}
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName, String parameterName) {
        return "INSERT INTO " + defaultTableName + " ";
    }

    /**
     * insert table()列
     *
     * @param entityClass 实体类
     * @param skipId      跳过id
     * @param notNull     非空
     * @param notEmpty    非空
     * @return {@link String}
     */
    public static String insertColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", notEmpty));
            } else {
                sql.append(column.getColumn()).append(",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * insert-values()列
     *
     * @param entityClass 实体类
     * @param skipId      跳过id
     * @param notNull     非空
     * @param notEmpty    非空
     * @return {@link String}
     */
    public static String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));
            } else {
                sql.append(column.getColumnHolder()).append(",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass 实体Class
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return XML中的SET语句块
     */
    public static String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //对乐观锁的支持
        EntityColumn versionColumn = null;
        // 逻辑删除列
        EntityColumn logicDeleteColumn = null;
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (versionColumn != null) {
                    throw new SystemException(entityClass.getName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                versionColumn = column;
            }
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new SystemException(entityClass.getName() + " 中包含多个带有 @LogicDelete 注解的字段，一个类中只能存在一个带有 @LogicDelete 注解的字段!");
                }
                logicDeleteColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column == versionColumn) {
                    Version version = versionColumn.getEntityField().getAnnotation(Version.class);
                    String versionClass = version.nextVersion().getName();
                    sql.append("<bind name=\"").append(column.getProperty()).append("Version\" value=\"");
                    //version = ${@tk.mybatis.mapper.version@nextVersionClass("versionClass", version)}
                    sql.append("@tk.mybatis.mapper.version.VersionUtil@nextVersion(")
                            .append("@").append(versionClass).append("@class, ");
                    if (StringUtils.isNotEmpty(entityName)) {
                        sql.append(entityName).append(".");
                    }
                    sql.append(column.getProperty()).append(")\"/>");
                    sql.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("Version},");
                } else if (column == logicDeleteColumn) {
                    sql.append(logicDeleteColumnEqualsValue(column, false)).append(",");
                } else if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName)).append(",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * update set列，不考虑乐观锁注解 @Version
     *
     * @param entityClass 实体类
     * @param entityName  实体名称
     * @param notNull     非空
     * @param notEmpty    非空
     * @return {@link String}
     */
    public static String updateSetColumnsIgnoreVersion(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        // 逻辑删除列
        EntityColumn logicDeleteColumn = null;
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new SystemException(entityClass.getName() + " 中包含多个带有 @LogicDelete 注解的字段，一个类中只能存在一个带有 @LogicDelete 注解的字段!");
                }
                logicDeleteColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column.getEntityField().isAnnotationPresent(Version.class)) {
                    //ignore
                } else if (column == logicDeleteColumn) {
                    sql.append(logicDeleteColumnEqualsValue(column, false)).append(",");
                } else if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName)).append(",");
                }
            } else if (column.isId() && column.isUpdatable()) {
                //set id = id,
                sql.append(column.getColumn()).append(" = ").append(column.getColumn()).append(",");
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 不是所有参数都是 null 的检查
     *
     * @param parameterName 参数名称
     * @param columnSet     列组
     * @return {@link String}
     */
    public static String notAllNullParameterCheck(String parameterName, Set<EntityColumn> columnSet) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"notAllNullParameterCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notAllNullParameterCheck(");
        sql.append(parameterName).append(", '");
        StringBuilder fields = new StringBuilder();
        for (EntityColumn column : columnSet) {
            if (fields.length() > 0) {
                fields.append(",");
            }
            fields.append(column.getProperty());
        }
        sql.append(fields);
        sql.append("')\"/>");
        return sql.toString();
    }

    /**
     * Example 中包含至少 1 个查询条件
     *
     * @param parameterName 参数名称
     * @return {@link String}
     */
    public static String exampleHasAtLeastOneCriteriaCheck(String parameterName) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"exampleHasAtLeastOneCriteriaCheck\" value=\"@tk.mybatis.mapper.util.OGNL@exampleHasAtLeastOneCriteriaCheck(");
        sql.append(parameterName).append(")\"/>");
        return sql.toString();
    }

    /**
     * where主键条件
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String wherePKColumns(Class<?> entityClass) {
        return wherePKColumns(entityClass, false);
    }

    /**
     * where主键条件
     *
     * @param entityClass 实体类
     * @param useVersion  使用版本
     * @return {@link String}
     */
    public static String wherePKColumns(Class<?> entityClass, boolean useVersion) {
        return wherePKColumns(entityClass, null, useVersion);
    }

    /**
     * where主键条件
     *
     * @param entityClass 实体类
     * @param entityName  实体名称
     * @param useVersion  使用版本
     * @return {@link String}
     */
    public static String wherePKColumns(Class<?> entityClass, String entityName, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        boolean hasLogicDelete = hasLogicDeleteColumn(entityClass);

        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getPKColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            sql.append(" AND ").append(column.getColumnEqualsHolder(entityName));
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }

        if (hasLogicDelete) {
            sql.append(whereLogicDelete(entityClass, false));
        }

        sql.append("</where>");
        return sql.toString();
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass 实体类
     * @param empty       空
     * @return {@link String}
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty) {
        return whereAllIfColumns(entityClass, empty, false);
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass 实体类
     * @param empty       空
     * @param useVersion  使用版本
     * @return {@link String}
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        boolean hasLogicDelete = false;

        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (!useVersion || !column.getEntityField().isAnnotationPresent(Version.class)) {
                // 逻辑删除，后面拼接逻辑删除字段的未删除条件
                if (logicDeleteColumn != null && logicDeleteColumn == column) {
                    hasLogicDelete = true;
                    continue;
                }
                sql.append(getIfNotNull(column, " AND " + column.getColumnEqualsHolder(), empty));
            }
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }
        if (hasLogicDelete) {
            sql.append(whereLogicDelete(entityClass, false));
        }

        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 乐观锁字段条件
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String whereVersion(Class<?> entityClass){
        return whereVersion(entityClass,null);
    }

    /**
     * 乐观锁字段条件
     *
     * @param entityClass 实体类
     * @param entityName  实体名称
     * @return {@link String}
     */
    public static String whereVersion(Class<?> entityClass,String entityName) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasVersion = false;
        String result = "";
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (hasVersion) {
                    throw new SystemException(entityClass.getName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                hasVersion = true;
                result = " AND " + column.getColumnEqualsHolder(entityName);
            }
        }
        return result;
    }

    /**
     * 逻辑删除的where条件，没有逻辑删除注解则返回空字符串
     * <br>
     * AND column = value
     *
     * @param entityClass 实体类
     * @param isDeleted   被删除
     * @return {@link String}
     */
    public static String whereLogicDelete(Class<?> entityClass, boolean isDeleted) {
        String value = logicDeleteColumnEqualsValue(entityClass, isDeleted);
        return "".equals(value) ? "" : " AND " + value;
    }

    /**
     * 返回格式: column = value
     * <br>
     * 默认isDeletedValue = 1  notDeletedValue = 0
     * <br>
     * 则返回is_deleted = 1 或 is_deleted = 0
     * <br>
     * 若没有逻辑删除注解，则返回空字符串
     *
     * @param entityClass 实体类
     * @param isDeleted   被删除
     * @return {@link String}
     */
    public static String logicDeleteColumnEqualsValue(Class<?> entityClass, boolean isDeleted) {
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);

        if (logicDeleteColumn != null) {
            return logicDeleteColumnEqualsValue(logicDeleteColumn, isDeleted);
        }

        return "";
    }

    /**
     * 返回格式: column = value
     * <br>
     * 默认isDeletedValue = 1  notDeletedValue = 0
     * <br>
     * 则返回is_deleted = 1 或 is_deleted = 0
     * <br>
     * 若没有逻辑删除注解，则返回空字符串
     *
     * @param column    列
     * @param isDeleted 被删除
     * @return {@link String}
     */
    public static String logicDeleteColumnEqualsValue(EntityColumn column, boolean isDeleted) {
        String result = "";
        if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
            result = column.getColumn() + " = " + getLogicDeletedValue(column, isDeleted);
        }
        return result;
    }

    /**
     * 获取逻辑删除注解的参数值
     *
     * @param column    列
     * @param isDeleted 被删除
     * @return int
     */
    public static int getLogicDeletedValue(EntityColumn column, boolean isDeleted) {
        if (!column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
            throw new SystemException(column.getColumn() + " 没有 @LogicDelete 注解!");
        }
        LogicDelete logicDelete = column.getEntityField().getAnnotation(LogicDelete.class);
        if (isDeleted) {
            return logicDelete.isDeletedValue();
        }
        return logicDelete.notDeletedValue();
    }

    /**
     * 是否有逻辑删除的注解
     *
     * @param entityClass 实体类
     * @return boolean
     */
    public static boolean hasLogicDeleteColumn(Class<?> entityClass) {
        return getLogicDeleteColumn(entityClass) != null;
    }

    /**
     * 获取逻辑删除注解的列，若没有返回null
     *
     * @param entityClass 实体类
     * @return {@link EntityColumn}
     */
    public static EntityColumn getLogicDeleteColumn(Class<?> entityClass) {
        EntityColumn logicDeleteColumn = null;
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasLogicDelete = false;
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (hasLogicDelete) {
                    throw new SystemException(entityClass.getName() + " 中包含多个带有 @LogicDelete 注解的字段，一个类中只能存在一个带有 @LogicDelete 注解的字段!");
                }
                hasLogicDelete = true;
                logicDeleteColumn = column;
            }
        }
        return logicDeleteColumn;
    }

    /**
     * example支持查询指定列时
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String exampleSelectColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(_parameter)\">");
        sql.append("<foreach collection=\"_parameter.selectColumns\" item=\"selectColumn\" separator=\",\">");
        sql.append("${selectColumn}");
        sql.append("</foreach>");
        sql.append("</when>");
        //不支持指定列的时候查询全部列
        sql.append("<otherwise>");
        sql.append(getAllColumns(entityClass));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * example支持查询指定列时
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String exampleCountColumn(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasCountColumn(_parameter)\">");
        sql.append("COUNT(<if test=\"distinct\">distinct </if>${countColumn})");
        sql.append("</when>");
        sql.append("<otherwise>");
        sql.append("COUNT(*)");
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * example 支持 for update
     *
     * @return {@link String}
     */
    public static String exampleForUpdate() {
        return "<if test=\"@tk.mybatis.mapper.util.OGNL@hasForUpdate(_parameter)\">" + "FOR UPDATE" + "</if>";
    }

    /**
     * example 支持 for update
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    public static String exampleCheck(Class<?> entityClass) {
        return "<bind name=\"checkExampleEntityClass\" value=\"@tk.mybatis.mapper.util.OGNL@checkExampleEntityClass(_parameter, '" + entityClass.getName() + "')\"/>";
    }

    /**
     * Example查询中的where结构，用于只有一个Example参数时
     *
     * @return {@link String}
     */
    public static String exampleWhereClause() {
        return "<if test=\"_parameter != null\">" +
                "<where>\n" +
                " ${@tk.mybatis.mapper.util.OGNL@andNotLogicDelete(_parameter)}" +
                " <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "  <foreach collection=\"oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                " </trim>\n" +
                "</where>" +
                "</if>";
    }

    /**
     * Example-Update中的where结构，用于多个参数时，Example带@Param("example")注解时
     *
     * @return {@link String}
     */
    public static String updateByExampleWhereClause() {
        return "<where>\n" +
                " ${@tk.mybatis.mapper.util.OGNL@andNotLogicDelete(example)}" +
                " <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "  <foreach collection=\"example.oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                " </trim>\n" +
                "</where>";
    }

}
