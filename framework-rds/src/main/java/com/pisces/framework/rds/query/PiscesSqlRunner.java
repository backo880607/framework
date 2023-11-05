package com.pisces.framework.rds.query;

import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.Connection;

/**
 * 双鱼座sql跑步
 *
 * @author jason
 * @date 2023/07/19
 */
public class PiscesSqlRunner extends SqlRunner {
    
    public PiscesSqlRunner(Connection connection) {
        super(connection);
    }
}
