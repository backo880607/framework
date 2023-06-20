package com.pisces.framework.core.utils;

/**
 * generator
 *
 * @author jason
 * @date 2022/12/07
 */
public final class IDGenerator {

    public static final IDGenerator instance = new IDGenerator();
    static SnowflakeIdGen snowflakeIdGen = new SnowflakeIdGen(1, 0);

    private IDGenerator() {
    }

    public long getID() {
        return snowflakeIdGen.nextId();
    }
}
