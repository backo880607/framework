package com.pisces.framework.rds.provider;

import com.pisces.framework.rds.helper.MapperHelper;
import com.pisces.framework.rds.helper.MapperTemplate;
import com.pisces.framework.rds.provider.base.*;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 基础提供者
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class BaseProvider extends MapperTemplate {
    private volatile SQLProvider provider;

    public BaseProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    protected SQLProvider getProvider(MappedStatement ms) {
        if (this.provider == null) {
            synchronized (BaseProvider.class) {
                if (this.provider == null) {
                    try (Connection conn = ms.getConfiguration().getEnvironment().getDataSource().getConnection()) {
                        DatabaseMetaData dbmd = conn.getMetaData();
                        if (dbmd == null) {
                            return null;
                        }
                        String dbName = dbmd.getDatabaseProductName();
                        if (dbName == null) {
                            return null;
                        }

                        if (dbName.startsWith("DB2/")) {
                            this.provider = new Db2Provider();
                        } else if (dbName.startsWith("Oracle")) {
                            this.provider = new OracleProvider();
                        } else if (dbName.startsWith("Microsoft SQL Server")) {
                            this.provider = new SqlServerProvider();
                        } else if ("Adaptive Server".equals(dbName)) {
                            this.provider = new AdaptiveProvider();
                        } else if ("MySQL".equals(dbName)) {
                            this.provider = new MySqlProvider();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return this.provider;
    }
}
