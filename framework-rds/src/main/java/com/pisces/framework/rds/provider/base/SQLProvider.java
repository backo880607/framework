package com.pisces.framework.rds.provider.base;

import com.pisces.framework.rds.helper.entity.EntityColumn;
import com.pisces.framework.type.PROPERTY_TYPE;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * sql provider
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class SQLProvider {

    /**
     * 得到sql type
     *
     * @param jdbcType jdbc类型
     * @return {@link String}
     */
    public abstract String getSqlType(JdbcType jdbcType);

    // 由数据库的类型获取内部支持的Jdbc数据类型。
    public boolean compatible(JdbcType lhs, JdbcType rhs) {
        return lhs == rhs;
    }

    public static JdbcType getJdbcType(PROPERTY_TYPE type, boolean large) {
        JdbcType jdbcType = JdbcType.OTHER;
        switch (type) {
            case BOOLEAN -> jdbcType = JdbcType.BIT;
            case SHORT -> jdbcType = JdbcType.SMALLINT;
            case INTEGER -> jdbcType = JdbcType.INTEGER;
            case LONG -> jdbcType = JdbcType.BIGINT;
            case DOUBLE -> jdbcType = JdbcType.DOUBLE;
            case DATE -> jdbcType = JdbcType.DATE;
            case TIME -> jdbcType = JdbcType.TIME;
            case DATE_TIME -> jdbcType = JdbcType.TIMESTAMP;
            case DURATION, ENUM, MULTI_ENUM -> jdbcType = JdbcType.VARCHAR;
            case STRING -> jdbcType = large ? JdbcType.LONGVARCHAR : JdbcType.VARCHAR;
            case BEAN, LIST -> jdbcType = JdbcType.LONGVARCHAR;
            default -> {
            }
        }
        if (jdbcType == JdbcType.OTHER) {
            throw new UnsupportedOperationException("not support property type: " + type.name());
        }
        return jdbcType;
    }

    /**
     * 得到司机名字
     *
     * @return {@link String}
     */
    public abstract String getDriverName();

    /**
     * 获得连接
     *
     * @param host     宿主
     * @param port     港口
     * @param dataBase 数据库
     * @param charset  字符集
     * @return {@link String}
     */
    public abstract String getConnection(String host, int port, String dataBase, String charset);

    /**
     * 存在数据库
     *
     * @param conn     康涅狄格州
     * @param dataBase 数据库
     * @return boolean
     * @throws SQLException sqlexception异常
     */
    public abstract boolean existedDataBase(Connection conn, String dataBase) throws SQLException;

    /**
     * 创建数据库
     *
     * @param conn     康涅狄格州
     * @param dataBase 数据库
     * @throws SQLException sqlexception异常
     */
    public abstract void createDataBase(Connection conn, String dataBase) throws SQLException;

    /**
     * 下降数据基础
     *
     * @param conn     康涅狄格州
     * @param dataBase 数据库
     * @throws SQLException sqlexception异常
     */
    public abstract void dropDataBase(Connection conn, String dataBase) throws SQLException;

    /**
     * 存在表
     *
     * @param conn      康涅狄格州
     * @param dataBase  数据库
     * @param tableName 表名
     * @return boolean
     * @throws SQLException sqlexception异常
     */
    public abstract boolean existedTable(Connection conn, String dataBase, String tableName) throws SQLException;

    /**
     * 创建表
     *
     * @param conn      康涅狄格州
     * @param tableName 表名
     * @param columns   列
     * @throws SQLException sqlexception异常
     */
    public abstract void createTable(Connection conn, String tableName, Collection<EntityColumn> columns) throws SQLException;

    /**
     * 删除表
     *
     * @param conn      康涅狄格州
     * @param tableName 表名
     * @throws SQLException sqlexception异常
     */
    public abstract void dropTable(Connection conn, String tableName) throws SQLException;

    /**
     * 添加列
     *
     * @param tableName 表名
     * @param columns   列
     * @return {@link String}
     */
    public abstract String addColumns(String tableName, Collection<EntityColumn> columns);

    /**
     * 改变列
     *
     * @param tableName 表名
     * @param columns   列
     * @return {@link String}
     */
    public abstract String changeColumns(String tableName, Collection<EntityColumn> columns);

    /**
     * 删除列
     *
     * @param tableName 表名
     * @param columns   列
     * @return {@link String}
     */
    public abstract String dropColumns(String tableName, Map<String, EntityColumn> columns);
}
