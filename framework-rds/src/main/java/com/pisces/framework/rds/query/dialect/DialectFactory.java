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

import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.rds.datasource.DynamicRoutingDataSource;
import com.pisces.framework.rds.datasource.support.DatabaseUtils;
import com.pisces.framework.rds.enums.DatabaseType;
import com.pisces.framework.rds.query.dialect.impl.CommonsDialectImpl;
import com.pisces.framework.rds.query.dialect.impl.OracleDialect;
import org.apache.ibatis.util.MapUtil;

import javax.sql.DataSource;
import java.util.EnumMap;
import java.util.Map;

/**
 * 方言工厂类，用于创建方言
 *
 * @author jason
 * @date 2023/06/27
 */
public class DialectFactory {

    /**
     * 数据库类型和方言的映射关系，可以通过其读取指定的方言，亦可能通过其扩展其他方言
     * 比如，在 mybatis-flex 实现的方言中有 bug 或者 有自己的独立实现，可以添加自己的方言实现到
     * 此 map 中，用于覆盖系统的方言实现
     */
    private static final Map<DatabaseType, IDialect> DIALECT_MAP = new EnumMap<>(DatabaseType.class);

    private static DatabaseType getDbType() {
        DataSource dataSource = AppUtils.getBean(DataSource.class);
        if (dataSource instanceof DynamicRoutingDataSource) {
            return ((DynamicRoutingDataSource) dataSource).determineDatabaseType();
        }
        return DatabaseUtils.getType(dataSource);
    }

    /**
     * 获取方言
     *
     * @return IDialect
     */
    public static IDialect getDialect() {
        DatabaseType dbType = getDbType();
        return MapUtil.computeIfAbsent(DIALECT_MAP, dbType, DialectFactory::createDialect);
    }

    private static IDialect createDialect(DatabaseType dbType) {
        return switch (dbType) {
            case MYSQL, H2, MARIADB, GBASE, OSCAR, XUGU, CLICK_HOUSE, OCEAN_BASE, CUBRID, GOLDILOCKS, CSIIDB ->
                    new CommonsDialectImpl(KeywordWrap.BACKQUOTE, LimitOffsetProcessor.MYSQL);
            case ORACLE -> new OracleDialect(LimitOffsetProcessor.ORACLE);
            case DM, GAUSS -> new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.ORACLE);
            case POSTGRE_SQL, SQLITE, HSQL, KINGBASE_ES, PHOENIX, SAP_HANA, IMPALA, HIGH_GO, VERTICA, REDSHIFT, OPENGAUSS, TDENGINE, UXDB ->
                    new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.POSTGRESQL);
            case ORACLE_12C -> new OracleDialect(LimitOffsetProcessor.DERBY);
            case FIREBIRD -> new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.DERBY);
            case SQLSERVER -> new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS, LimitOffsetProcessor.DERBY);
            case SQLSERVER_2005 -> new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS, LimitOffsetProcessor.DB2);
            case INFORMIX -> new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.INFORMIX);
            case DB2 -> new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.DB2);
            case SYBASE -> new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.SYBASE);
            default -> new CommonsDialectImpl();
        };
    }
}
