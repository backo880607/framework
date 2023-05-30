package com.pisces.framework.rds.provider;

import com.pisces.framework.rds.entity.EntityColumn;
import com.pisces.framework.rds.entity.EntityTable;
import com.pisces.framework.rds.helper.EntityHelper;
import com.pisces.framework.rds.helper.MapperHelper;
import com.pisces.framework.rds.helper.MapperTemplate;
import com.pisces.framework.rds.mapper.CustomizeMapper;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 自定义提供程序
 *
 * @author jason
 * @date 2022/12/07
 */
public class CustomizeProvider extends BaseProvider {

    public CustomizeProvider(Class<?> mapperClazz, MapperHelper mapperHelper) {
        super(mapperClazz, mapperHelper);
    }

    private static boolean notChecked = true;

    public void checkTable(MappedStatement ms) throws SQLException {
        if (notChecked) {
            notChecked = false;
//            ms.getConfiguration().setObjectWrapperFactory(new EntityWrapperFactory());
//            ms.getConfiguration().setObjectFactory(new MybatisEntityFactory());
//            Transmit.instance.start();
            return;
        }
        Class<?> entityClass = getEntityClass(ms);
        try (Connection conn = ms.getConfiguration().getEnvironment().getDataSource().getConnection()) {
            if (!doExistedTable(conn, ms)) {
                doCreateTable(conn, ms);
                return;
            }

            Map<String, EntityColumn> existedColumns = getExistedColumns(conn, entityClass);
            List<EntityColumn> addColumns = new ArrayList<>();
            List<EntityColumn> changeColumns = new ArrayList<>();
            for (EntityColumn column : EntityHelper.getColumns(entityClass)) {
                EntityColumn existedColumn = existedColumns.get(column.getColumn());
                if (existedColumn != null) {
                    if (!(getProvider(ms).compatible(column.getJdbcType(), existedColumn.getJdbcType()))) {
                        changeColumns.add(column);
                    }
                    existedColumns.remove(column.getColumn());
                } else {
                    addColumns.add(column);
                }
            }

            if (!addColumns.isEmpty()) {
                autoAddColumns(conn, ms, addColumns);
            }

            if (!changeColumns.isEmpty()) {
                autoChangeColumns(conn, ms, changeColumns);
            }
        }
    }

    private boolean doExistedTable(Connection conn, MappedStatement ms) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        return getProvider(ms).existedTable(conn, conn.getCatalog(), tableName(entityClass));
    }

    private void doCreateTable(Connection conn, MappedStatement ms) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        this.getProvider(ms).createTable(conn, tableName(entityClass), table.getEntityClassColumns());
    }

    private Map<String, EntityColumn> getExistedColumns(Connection conn, Class<?> entityClass) throws SQLException {
        Map<String, EntityColumn> existedColumns = new HashMap<>(16);
        try (ResultSet resultSet = conn.getMetaData().getColumns(conn.getCatalog(), null, tableName(entityClass), null)) {
            while (resultSet.next()) {
                EntityColumn column = new EntityColumn();
                column.setColumn(resultSet.getString(4));
                column.setJdbcType(JdbcType.forCode(resultSet.getInt("DATA_TYPE")));
                existedColumns.put(column.getColumn(), column);
            }
        }
        return existedColumns;
    }

    private void autoAddColumns(Connection conn, MappedStatement ms, List<EntityColumn> columns) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        final String addSQL = this.getProvider(ms).addColumns(tableName(entityClass), columns);
        try (PreparedStatement stmt = conn.prepareStatement(addSQL)) {
            stmt.execute();
        }
    }

    private void autoChangeColumns(Connection conn, MappedStatement ms, List<EntityColumn> columns) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        final String changeSQL = this.getProvider(ms).changeColumns(tableName(entityClass), columns);
        try (PreparedStatement stmt = conn.prepareStatement(changeSQL)) {
            stmt.execute();
        }
    }

    private void autoDropColumns(Connection conn, MappedStatement ms, Map<String, EntityColumn> columns) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        final String changeSQL = this.getProvider(ms).dropColumns(tableName(entityClass), columns);
        try (PreparedStatement stmt = conn.prepareStatement(changeSQL)) {
            stmt.execute();
        }
    }
}
