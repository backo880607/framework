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
package com.pisces.framework.rds.query.dialect;

import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.rds.enums.DatabaseType;

/**
 * limit 和 offset 参数的处理器
 *
 * @author jason
 * @date 2023/06/27
 */
public interface LimitOffsetProcessor {

    /**
     * MySql 的处理器
     * 适合 {@link DatabaseType#MYSQL, DatabaseType#MARIADB, DatabaseType#H2, DatabaseType#CLICK_HOUSE, DatabaseType#XCloud}
     */
    LimitOffsetProcessor MYSQL = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            sql.append(" LIMIT ").append(limitOffset).append(", ").append(limitRows);
        } else if (limitRows != null) {
            sql.append(" LIMIT ").append(limitRows);
        }
        return sql;
    };

    /**
     * Postgresql 的处理器
     * 适合  {@link DatabaseType#POSTGRE_SQL, DatabaseType#SQLITE, DatabaseType#H2, DatabaseType#HSQL, DatabaseType#KINGBASE_ES, DatabaseType#PHOENIX}
     * 适合  {@link DatabaseType#SAP_HANA, DatabaseType#IMPALA, DatabaseType#HIGH_GO, DatabaseType#VERTICA, DatabaseType#REDSHIFT}
     * 适合  {@link DatabaseType#OPENGAUSS, DatabaseType#TDENGINE, DatabaseType#UXDB}
     */
    LimitOffsetProcessor POSTGRESQL = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            sql.append(" LIMIT ").append(limitRows).append(" OFFSET ").append(limitOffset);
        } else if (limitRows != null) {
            sql.append(" LIMIT ").append(limitRows);
        }
        return sql;
    };

    /**
     * derby 的处理器
     * 适合  {@link DatabaseType#DERBY, DatabaseType#ORACLE_12C, DatabaseType#SQLSERVER ,DBType#POSTGRE_SQL}
     */
    LimitOffsetProcessor DERBY = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // OFFSET ** ROWS FETCH NEXT ** ROWS ONLY")
            sql.append(" OFFSET ").append(limitOffset).append("  ROWS FETCH NEXT ").append(limitRows).append(" ROWS ONLY");
        } else if (limitRows != null) {
            // FETCH FIRST 20 ROWS ONLY
            sql.append(" FETCH FIRST ").append(limitRows).append(" ROWS ONLY");
        }
        return sql;
    };

    /**
     * db2 的处理器
     * 适合  {@link DatabaseType#DB2, DatabaseType#SQLSERVER_2005}
     */
    LimitOffsetProcessor DB2 = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // OFFSET ** ROWS FETCH NEXT ** ROWS ONLY")
            sql.append(" OFFSET ").append(limitOffset).append("  ROWS FETCH NEXT ").append(limitRows).append(" ROWS ONLY");
        } else if (limitRows != null) {
            // FETCH FIRST 20 ROWS ONLY
            sql.append(" FETCH FIRST ").append(limitRows).append(" ROWS ONLY");
        }
        return sql;
    };

    /**
     * Informix 的处理器
     * 适合  {@link DatabaseType#INFORMIX}
     * 文档 {@link <a href="https://www.ibm.com/docs/en/informix-servers/14.10?topic=clause-restricting-return-values-skip-limit-first-options">https://www.ibm.com/docs/en/informix-servers/14.10?topic=clause-restricting-return-values-skip-limit-first-options</a>}
     */
    LimitOffsetProcessor INFORMIX = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // SELECT SKIP 2 FIRST 1 * FROM
            sql.insert(6, " SKIP " + limitOffset + " FIRST " + limitRows);
        } else if (limitRows != null) {
            sql.insert(6, " FIRST " + limitRows);
        }
        return sql;
    };

    /**
     * Firebird 的处理器
     * 适合  {@link DatabaseType#FIREBIRD}
     */
    LimitOffsetProcessor FIREBIRD = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // ROWS 2 TO 3
            sql.append(" ROWS ").append(limitOffset).append(" TO ").append(limitOffset + limitRows);
        } else if (limitRows != null) {
            sql.insert(6, " FIRST " + limitRows);
        }
        return sql;
    };

    /**
     * Oracle11g及以下数据库的处理器
     * 适合  {@link DatabaseType#ORACLE, DatabaseType#DM, DatabaseType#GAUSS}
     */
    LimitOffsetProcessor ORACLE = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null) {
            if (limitOffset == null) {
                limitOffset = 0;
            }
            StringBuilder newSql = new StringBuilder("SELECT * FROM (SELECT TEMP_DATAS.*, ROWNUM RN FROM (");
            newSql.append(sql);
            newSql.append(") TEMP_DATAS WHERE  ROWNUM <=").append(limitOffset + limitRows).append(") WHERE RN >").append(limitOffset);
            return newSql;
        }
        return sql;
    };

    /**
     * Sybase 处理器
     * 适合  {@link DatabaseType#SYBASE}
     */
    LimitOffsetProcessor SYBASE = (sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            //SELECT TOP 1 START AT 3 * FROM
            sql.insert(6, " TOP " + limitRows + " START AT " + (limitOffset + 1));
        } else if (limitRows != null) {
            sql.insert(6, " TOP " + limitRows);
        }
        return sql;
    };


    /**
     * 处理构建 limit 和 offset
     *
     * @param sql          已经构建的 sql
     * @param queryWrapper 参数内容
     * @param limitRows    用户传入的 limit 参数 可能为 null
     * @param limitOffset  用户传入的 offset 参数，可能为 null
     */
    StringBuilder process(StringBuilder sql, QueryWrapper queryWrapper, Integer limitRows, Integer limitOffset);
}
