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
}
