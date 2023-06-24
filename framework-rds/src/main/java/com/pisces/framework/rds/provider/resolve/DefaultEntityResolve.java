package com.pisces.framework.rds.provider.resolve;

import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.annotation.ColumnType;
import com.pisces.framework.rds.helper.FieldHelper;
import com.pisces.framework.rds.helper.entity.Config;
import com.pisces.framework.rds.helper.entity.EntityColumn;
import com.pisces.framework.rds.helper.entity.EntityField;
import com.pisces.framework.rds.helper.entity.EntityTable;
import com.pisces.framework.rds.utils.MetaObjectUtil;
import com.pisces.framework.rds.utils.SimpleTypeUtil;
import com.pisces.framework.rds.utils.SqlReservedWords;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author liuzh
 */
public class DefaultEntityResolve implements EntityResolve {
    private final Log log = LogFactory.getLog(DefaultEntityResolve.class);

    @Override
    public EntityTable resolveEntity(Class<?> entityClass, Config config) {
        //创建并缓存EntityTable
        EntityTable entityTable = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!"".equals(table.name())) {
                entityTable = new EntityTable(entityClass);
                entityTable.setTable(table);
            }
        }
        if (entityTable == null) {
            entityTable = new EntityTable(entityClass);
            String tableName = MetaObjectUtil.convertByStyle(entityClass.getSimpleName());
            //自动处理关键字
            if (StringUtils.isNotEmpty(config.getWrapKeyword()) && SqlReservedWords.containsWord(tableName)) {
                tableName = MessageFormat.format(config.getWrapKeyword(), tableName);
            }
            entityTable.setName(tableName);
        }
        entityTable.setEntityClassColumns(new LinkedHashSet<>());
        entityTable.setEntityClassPKColumns(new LinkedHashSet<>());
        //处理所有列
        List<EntityField> fields = FieldHelper.getFields(entityClass);
        for (EntityField field : fields) {
            //如果启用了简单类型，就做简单类型校验，如果不是简单类型，直接跳过
            //3.5.0 如果启用了枚举作为简单类型，就不会自动忽略枚举类型
            //4.0 如果标记了 Column 或 ColumnType 注解，也不忽略
            if (SimpleTypeUtil.isSimpleType(field.getJavaType()) //开启简单类型时只处理包含的简单类型
                    || field.isAnnotationPresent(Column.class) //有注解的处理，不考虑类型
                    || field.isAnnotationPresent(ColumnType.class) //有注解的处理，不考虑类型
                    || Enum.class.isAssignableFrom(field.getJavaType())) { //开启枚举作为简单类型时处理
                processField(entityTable, field, config);
            }
        }
        //当pk.size=0的时候使用所有列作为主键
        if (entityTable.getEntityClassPKColumns().size() == 0) {
            entityTable.setEntityClassPKColumns(entityTable.getEntityClassColumns());
        }
        entityTable.initPropertyMap();
        return entityTable;
    }

    /**
     * 过程领域
     *
     * @param entityTable 实体表
     * @param field       场
     * @param config      配置
     */
    protected void processField(EntityTable entityTable, EntityField field, Config config) {
        //排除字段
        if (field.isAnnotationPresent(Transient.class)) {
            return;
        }
        //Id
        EntityColumn entityColumn = new EntityColumn(entityTable);
        //记录 field 信息，方便后续扩展使用
        entityColumn.setEntityField(field);
        if ("id".equalsIgnoreCase(field.getName())) {
            entityColumn.setId(true);
        }
        //Column
        String columnName = null;
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name();
            entityColumn.setUpdatable(column.updatable());
            entityColumn.setInsertable(column.insertable());
        }
        //ColumnType
        if (field.isAnnotationPresent(ColumnType.class)) {
            ColumnType columnType = field.getAnnotation(ColumnType.class);
            //是否为 blob 字段
            entityColumn.setBlob(columnType.isBlob());
            //column可以起到别名的作用
            if (StringUtils.isEmpty(columnName) && StringUtils.isNotEmpty(columnType.column())) {
                columnName = columnType.column();
            }
            if (columnType.jdbcType() != JdbcType.UNDEFINED) {
                entityColumn.setJdbcType(columnType.jdbcType());
            }
            if (columnType.typeHandler() != UnknownTypeHandler.class) {
                entityColumn.setTypeHandler(columnType.typeHandler());
            }
        }
        //列名
        if (StringUtils.isEmpty(columnName)) {
            columnName = MetaObjectUtil.convertByStyle(field.getName());
        }
        //自动处理关键字
        if (StringUtils.isNotEmpty(config.getWrapKeyword()) && SqlReservedWords.containsWord(columnName)) {
            columnName = MessageFormat.format(config.getWrapKeyword(), columnName);
        }
        entityColumn.setProperty(field.getName());
        entityColumn.setColumn(columnName);
        entityColumn.setJavaType(field.getJavaType());
        if (field.getJavaType().isPrimitive()) {
            log.warn("通用 Mapper 警告信息: <[" + entityColumn + "]> 使用了基本类型，基本类型在动态 SQL 中由于存在默认值，因此任何时候都不等于 null，建议修改基本类型为对应的包装类型!");
        }
        entityTable.getEntityClassColumns().add(entityColumn);
        if (entityColumn.isId()) {
            entityTable.getEntityClassPKColumns().add(entityColumn);
        }
    }
}
