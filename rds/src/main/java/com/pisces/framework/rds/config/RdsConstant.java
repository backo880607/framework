package com.pisces.framework.rds.config;

/**
 * rds常数
 *
 * @author jason
 * @date 2022/12/07
 */
public class RdsConstant {
    public static final String IDENTIFY = "rds";

    public static final String NAME = "MyBatis-Flex";
    public static final String VERSION = "1.2.6";
    public static final String DEFAULT_PRIMARY_FIELD = "id";

    public static final String SQL = "$$sql";
    public static final String SQL_ARGS = "$$sql_args";
    public static final String TABLE_NAME = "$$tableName";
    public static final String PRIMARY_KEY = "$$primaryKey";
    public static final String PRIMARY_VALUE = "$$primaryValue";

    public static final String QUERY = "$$query";
    public static final String ROW = "$$row";
    public static final String ROWS = "$$rows";

    public static final String ENTITY = "$$entity";
    public static final String ENTITIES = "$$entities";
    public static final String IGNORE_NULLS = "$$ignoreNulls";

    public static final String METHOD_INSERT_BATCH = "insertBatch";
    public static final String METHOD_SELECT_LIST_BY_QUERY_AS = "selectListByQueryAs";

    /**
     * 当 entity 使用逻辑删除时，0 为 entity 的正常状态
     */
    public static final int LOGIC_DELETE_NORMAL = 0;
    /**
     * 当 entity 使用逻辑删除时，1 为 entity 的删除状态
     */
    public static final int LOGIC_DELETE_DELETED = 1;
}
