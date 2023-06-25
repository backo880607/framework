package com.pisces.framework.rds.provider.resolve;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.utils.lang.ClassUtils;
import com.pisces.framework.rds.handler.MultiEnumTypeHandler;
import com.pisces.framework.rds.helper.entity.Config;
import com.pisces.framework.rds.helper.entity.EntityColumn;
import com.pisces.framework.rds.helper.entity.EntityField;
import com.pisces.framework.rds.helper.entity.EntityTable;
import com.pisces.framework.rds.provider.base.SQLProvider;
import com.pisces.framework.rds.utils.MetaObjectUtil;
import com.pisces.framework.type.MultiEnum;
import com.pisces.framework.type.PROPERTY_TYPE;
import com.pisces.framework.type.annotation.PropertyMeta;
import jakarta.persistence.Transient;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Set;

/**
 * 自定义实体解决
 *
 * @author jason
 * @date 2022/12/07
 */
public class CustomizeEntityResolve extends DefaultEntityResolve {
    @Override
    public EntityTable resolveEntity(Class<?> entityClass, Config config) {
        EntityTable table = super.resolveEntity(entityClass, config);
        try {
            modifyColumns(entityClass, table);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    private void modifyColumns(Class<?> entityClass, EntityTable table) throws Exception {
        Set<EntityColumn> columns = table.getEntityClassColumns();
        for (EntityColumn column : columns) {
            EntityField field = column.getEntityField();
            if (field.getJavaType() == Locale.class) {
                column.setJdbcType(JdbcType.VARCHAR);
            } else {
                PROPERTY_TYPE type = ClassUtils.getPropertyType(field.getJavaType());
                boolean large = false;
                PropertyMeta meta = field.getAnnotation(PropertyMeta.class);
                if (meta != null) {
                    if (meta.type() != PROPERTY_TYPE.NONE) {
                        type = meta.type();
                    }
                    large = meta.large();
                }
                if (type == PROPERTY_TYPE.ENUM) {
                    column.setJdbcType(JdbcType.VARCHAR);
                } else {
                    column.setJdbcType(SQLProvider.getJdbcType(type, large));
                }
            }
        }
        Class<?> clazz = entityClass;
        while (clazz != BeanObject.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()) && MultiEnum.class.isAssignableFrom(field.getType())) {
                    Transient tran = field.getAnnotation(Transient.class);
                    if (tran == null) {
                        EntityColumn column = new EntityColumn(table);
                        column.setColumn(MetaObjectUtil.convertByStyle(field.getName()));
                        column.setProperty(field.getName());
                        column.setJavaType(field.getType());
                        column.setJdbcType(JdbcType.INTEGER);
                        column.setTypeHandler(MultiEnumTypeHandler.class);
                        EntityField entityField = new EntityField(null, null);
                        modify(entityField, "name", field.getName());
                        modify(entityField, "field", field);
                        modify(entityField, "javaType", field.getType());
                        modify(entityField, "setter", null);
                        modify(entityField, "getter", null);
                        column.setEntityField(entityField);
                        columns.add(column);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        table.initPropertyMap();
    }

    private static void modify(EntityField entityField, String fieldName, Object object) throws Exception {
        Field field = EntityField.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(entityField, object);
        field.setAccessible(false);
    }
}
