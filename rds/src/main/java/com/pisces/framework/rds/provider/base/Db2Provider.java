package com.pisces.framework.rds.provider.base;

import com.pisces.framework.rds.entity.EntityColumn;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * db2提供程序
 *
 * @author jason
 * @date 2022/12/07
 */
public class Db2Provider extends SQLProvider {

    @Override
    public String getSqlType(JdbcType jdbcType) {
        switch (jdbcType) {
            case ARRAY:
            case BIT:
            case TINYINT:
            case SMALLINT:
            case INTEGER:
                return "int";
            case BIGINT:
                return "";
            case FLOAT:
                return "";
            case REAL:
                return "";
            case DOUBLE:
                return "";
            case NUMERIC:
                return "";
            case DECIMAL:
                return "";
            case CHAR:
                return "";
            case VARCHAR:
                return "";
            case LONGVARCHAR:
            case DATE:
            case TIME:
            case TIMESTAMP:
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
            case NULL:
            case OTHER:
            case BLOB:
            case CLOB:
            case BOOLEAN:
            case CURSOR:
            case UNDEFINED:
            case NVARCHAR:
            case NCHAR:
            case NCLOB:
            case STRUCT:
            case JAVA_OBJECT:
            case DISTINCT:
            case REF:
            case DATALINK:
            case ROWID:
            case LONGNVARCHAR:
            case SQLXML:
            case DATETIMEOFFSET:
            default:
                break;
        }
        throw new UnsupportedOperationException("not support jdbc type: " + jdbcType.name());
    }

    @Override
    public String getDriverName() {
        return null;
    }

    @Override
    public String getConnection(String host, int port, String dataBase, String charset) {
        return null;
    }

    @Override
    public boolean existedDataBase(Connection conn, String dataBase) throws SQLException {
        return false;
    }

    @Override
    public void createDataBase(Connection conn, String dataBase) throws SQLException {

    }

    @Override
    public void dropDataBase(Connection conn, String dataBase) throws SQLException {

    }

    @Override
    public boolean existedTable(Connection conn, String dataBase, String tableName) throws SQLException {
        return false;
    }

    @Override
    public void createTable(Connection conn, String tableName, Collection<EntityColumn> columns) throws SQLException {

    }

    @Override
    public void dropTable(Connection conn, String tableName) throws SQLException {

    }

    @Override
    public String addColumns(String tableName, Collection<EntityColumn> columns) {
        return null;
    }

    @Override
    public String changeColumns(String tableName, Collection<EntityColumn> columns) {
        return null;
    }

    @Override
    public String dropColumns(String tableName, Map<String, EntityColumn> columns) {
        return null;
    }
}
