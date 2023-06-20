package com.pisces.framework.rds.config;

import com.pisces.framework.rds.enums.DBType;

/**
 * rds上下文
 *
 * @author jason
 * @date 2022/12/07
 */
public class RdsContext {
    private static final ThreadLocal<DBType> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(DBType dbTypeEnum) {
        CONTEXT_HOLDER.set(dbTypeEnum);
    }

    public static DBType get() {
        return CONTEXT_HOLDER.get();
    }

    public static void master() {
        set(DBType.MASTER);
    }

    public static void slave() {
        set(DBType.SLAVE);
    }

    /**
     * 数据源：主库
     */
    public static final String MASTER = "master";
    /**
     * 数据源：从库
     */
    public static final String SLAVE = "slave";

    /**
     * DRUID数据源类
     */
    public static final String DRUID_DATASOURCE = "com.alibaba.druid.pool.DruidDataSource";
    /**
     * HikariCp数据源
     */
    public static final String HIKARI_DATASOURCE = "com.zaxxer.hikari.HikariDataSource";
    /**
     * BeeCp数据源
     */
    public static final String BEECP_DATASOURCE = "cn.beecp.BeeDataSource";
    /**
     * DBCP2数据源
     */
    public static final String DBCP2_DATASOURCE = "org.apache.commons.dbcp2.BasicDataSource";
    /**
     * Atomikos数据源
     */
    public static final String ATOMIKOS_DATASOURCE = "com.atomikos.jdbc.AtomikosDataSourceBean";
}
