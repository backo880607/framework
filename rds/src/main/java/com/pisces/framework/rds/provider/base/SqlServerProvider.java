package com.pisces.framework.rds.provider.base;

import com.pisces.framework.rds.entity.EntityColumn;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * sql server提供者
 *
 * @author jason
 * @date 2022/12/07
 */
public class SqlServerProvider extends SQLProvider {

    @Override
    public String getSqlType(JdbcType jdbcType) {
        switch (jdbcType) {
            case BOOLEAN:
            case BIT:
                return "bit";
            case TINYINT:
                return "tinyint";
            case SMALLINT:
                return "smallint";
            case INTEGER:
                return "int";
            case BIGINT:
                return "bigint";
            case FLOAT:
                return "float";
            case REAL:
                return "real";
            case DOUBLE:
                return "float (2005,2008)";
            case NUMERIC:
                return "numeric(18, 0)";
            case DECIMAL:
                return "decimal(18, 0)";
            case CHAR:
                return "char";
            case VARCHAR:
                return "varchar(255)";
            case DATE:
                return "date";
            case TIME:
                return "time(7)";
            case TIMESTAMP:
                return "datetime";
            case BINARY:
                return "binary(50)";
            case VARBINARY:
                return "varbinary(50)";
            case LONGVARBINARY:
                return "varbinary(MAX)";
            case NULL:
            case OTHER:
            case BLOB:
            case CLOB:
            case LONGVARCHAR:
                return "text";
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
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getConnection(String host, int port, String dataBase, String charset) {
        StringBuilder sql = new StringBuilder("jdbc:sqlserver://");
        sql.append(host).append(":").append(port).append(";DatabaseName=").append(dataBase);
        return sql.toString();
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
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM sysobjects WHERE id = object_id('");
        sql.append(tableName).append("')");
        try (Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql.toString());
            if (resultSet != null && resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createTable(Connection conn, String tableName, Collection<EntityColumn> columns) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (");
        for (EntityColumn column : columns) {
            sql.append(column.getColumn()).append(" ").append(getSqlType(column.getJdbcType())).append(",");
        }
        sql.append("PRIMARY KEY (id))");
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        }
    }

    @Override
    public void dropTable(Connection conn, String tableName) throws SQLException {
        StringBuilder sql = new StringBuilder("DROP TABLE ");
        sql.append(tableName);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        }
    }

    @Override
    public String addColumns(String tableName, Collection<EntityColumn> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD ");
        boolean bFind = false;
        for (EntityColumn column : columns) {
            if (bFind) {
                sql.append(",");
            }
            sql.append(column.getColumn()).append(" ").append(getSqlType(column.getJdbcType()));
            bFind = true;
        }
        return sql.toString();
    }

    @Override
    public String changeColumns(String tableName, Collection<EntityColumn> columns) {
        StringBuilder sql = new StringBuilder();
        for (EntityColumn column : columns) {
            sql.append("ALTER TABLE ").append(tableName).append(" MODIFY ");
            sql.append(column.getColumn()).append(" ").append(getSqlType(column.getJdbcType()));
            sql.append(";");
        }
        return sql.toString();
    }

    @Override
    public String dropColumns(String tableName, Map<String, EntityColumn> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" DROP ");
        boolean bFind = false;
        for (Entry<String, EntityColumn> entry : columns.entrySet()) {
            if (bFind) {
                sql.append(",");
            }
            sql.append("COLUMN ").append(entry.getKey());
            bFind = true;
        }
        return sql.toString();
    }
}
