/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pisces.framework.rds.datasource.support;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.enums.DatabaseType;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.regex.Pattern;

/**
 * DBType 解析 工具类
 */
public class DatabaseUtils {

    /**
     * 获取当前配置的 DBType
     */
    public static DatabaseType getType(DataSource dataSource) {
        String jdbcUrl = getJdbcUrl(dataSource);

        if (StringUtils.isNotBlank(jdbcUrl)) {
            return parseDbType(jdbcUrl);
        }

        throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
    }

    /**
     * 通过数据源中获取 jdbc 的 url 配置
     * 符合 HikariCP, druid, c3p0, DBCP, beecp 数据源框架 以及 MyBatis UnpooledDataSource 的获取规则
     *
     * @return jdbc url 配置
     */
    public static String getJdbcUrl(DataSource dataSource) {
        String[] methodNames = new String[]{"getUrl", "getJdbcUrl"};
        for (String methodName : methodNames) {
            try {
                Method method = dataSource.getClass().getMethod(methodName);
                return (String) method.invoke(dataSource);
            } catch (Exception e) {
                //ignore
            }
        }

        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw new SystemException("Can not get the dataSource jdbcUrl", e);
        }
        //ignore
    }


    /**
     * 参考 druid  和 MyBatis-plus 的 JdbcUtils
     *
     * @param jdbcUrl jdbcURL
     * @return 返回数据库类型
     */
    private static DatabaseType parseDbType(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DatabaseType.MYSQL;
        } else if (jdbcUrl.contains(":mariadb:")) {
            return DatabaseType.MARIADB;
        } else if (jdbcUrl.contains(":oracle:")) {
            return DatabaseType.ORACLE;
        } else if (jdbcUrl.contains(":sqlserver:") || jdbcUrl.contains(":microsoft:")) {
            return DatabaseType.SQLSERVER_2005;
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return DatabaseType.SQLSERVER;
        } else if (jdbcUrl.contains(":postgresql:")) {
            return DatabaseType.POSTGRE_SQL;
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return DatabaseType.HSQL;
        } else if (jdbcUrl.contains(":db2:")) {
            return DatabaseType.DB2;
        } else if (jdbcUrl.contains(":sqlite:")) {
            return DatabaseType.SQLITE;
        } else if (jdbcUrl.contains(":h2:")) {
            return DatabaseType.H2;
        } else if (isMatchedRegex(":dm\\d*:", jdbcUrl)) {
            return DatabaseType.DM;
        } else if (jdbcUrl.contains(":xugu:")) {
            return DatabaseType.XUGU;
        } else if (isMatchedRegex(":kingbase\\d*:", jdbcUrl)) {
            return DatabaseType.KINGBASE_ES;
        } else if (jdbcUrl.contains(":phoenix:")) {
            return DatabaseType.PHOENIX;
        } else if (jdbcUrl.contains(":zenith:")) {
            return DatabaseType.GAUSS;
        } else if (jdbcUrl.contains(":gbase:")) {
            return DatabaseType.GBASE;
        } else if (jdbcUrl.contains(":gbasedbt-sqli:") || jdbcUrl.contains(":informix-sqli:")) {
            return DatabaseType.GBASE_8S;
        } else if (jdbcUrl.contains(":ch:") || jdbcUrl.contains(":clickhouse:")) {
            return DatabaseType.CLICK_HOUSE;
        } else if (jdbcUrl.contains(":oscar:")) {
            return DatabaseType.OSCAR;
        } else if (jdbcUrl.contains(":sybase:")) {
            return DatabaseType.SYBASE;
        } else if (jdbcUrl.contains(":oceanbase:")) {
            return DatabaseType.OCEAN_BASE;
        } else if (jdbcUrl.contains(":highgo:")) {
            return DatabaseType.HIGH_GO;
        } else if (jdbcUrl.contains(":cubrid:")) {
            return DatabaseType.CUBRID;
        } else if (jdbcUrl.contains(":goldilocks:")) {
            return DatabaseType.GOLDILOCKS;
        } else if (jdbcUrl.contains(":csiidb:")) {
            return DatabaseType.CSIIDB;
        } else if (jdbcUrl.contains(":sap:")) {
            return DatabaseType.SAP_HANA;
        } else if (jdbcUrl.contains(":impala:")) {
            return DatabaseType.IMPALA;
        } else if (jdbcUrl.contains(":vertica:")) {
            return DatabaseType.VERTICA;
        } else if (jdbcUrl.contains(":xcloud:")) {
            return DatabaseType.XCloud;
        } else if (jdbcUrl.contains(":firebirdsql:")) {
            return DatabaseType.FIREBIRD;
        } else if (jdbcUrl.contains(":redshift:")) {
            return DatabaseType.REDSHIFT;
        } else if (jdbcUrl.contains(":opengauss:")) {
            return DatabaseType.OPENGAUSS;
        } else if (jdbcUrl.contains(":taos:") || jdbcUrl.contains(":taos-rs:")) {
            return DatabaseType.TDENGINE;
        } else if (jdbcUrl.contains(":informix")) {
            return DatabaseType.INFORMIX;
        } else if (jdbcUrl.contains(":uxdb:")) {
            return DatabaseType.UXDB;
        } else if (jdbcUrl.contains(":greenplum:")) {
            return DatabaseType.GREENPLUM;
        } else {
            return DatabaseType.OTHER;
        }
    }

    /**
     * 正则匹配，验证成功返回 true，验证失败返回 false
     */
    public static boolean isMatchedRegex(String regex, String jdbcUrl) {
        if (null == jdbcUrl) {
            return false;
        }
        return Pattern.compile(regex).matcher(jdbcUrl).find();
    }
}
