package com.pisces.framework.rds.query;

import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.QueryOrderBy;
import com.pisces.framework.core.query.QueryTable;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.utils.lang.ClassUtils;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.config.RdsConstant;
import com.pisces.framework.rds.helper.EntityHelper;
import com.pisces.framework.rds.helper.entity.EntityTable;
import com.pisces.framework.rds.query.dialect.DialectFactory;
import com.pisces.framework.rds.query.dialect.IDialect;

import java.lang.reflect.Array;
import java.util.List;

/**
 * sql工具
 *
 * @author jason
 * @date 2023/06/28
 */
public class SqlTools {

    private static String getTableName(QueryTable queryTable) {
        if (queryTable.getBeanClass() == null) {
            return "";
        }
        return EntityHelper.getEntityTable(queryTable.getBeanClass()).getName();
    }

    private static String getColumnName(QueryColumn column) {
        QueryTable queryTable = column.getTable();
        if (queryTable == null || queryTable.getBeanClass() == null) {
            return "";
        }
        EntityTable entityTable = EntityHelper.getEntityTable(column.getTable().getBeanClass());
        return entityTable.getColumn(column.getName()).getColumn();
    }

    private static String getColumnTableName(List<QueryTable> queryTables, QueryTable queryTable) {
        if (queryTables == null) {
            return "";
        }

        if (queryTables.size() == 1 && queryTables.get(0).isSame(queryTable)) {
            return "";
        }

        QueryTable realTable = getRealTable(queryTables, queryTable);
        if (realTable == null) {
            return "";
        }

        return getTableName(realTable);
    }

    private static QueryTable getRealTable(List<QueryTable> queryTables, QueryTable queryTable) {
        if (CollectionUtils.isEmpty(queryTables)) {
            return queryTable;
        }

        if (queryTable == null && queryTables.size() == 1) {
            return queryTables.get(0);
        }

        for (QueryTable table : queryTables) {
            if (table.isSame(queryTable)) {
                return table;
            }
        }
        return queryTable;
    }

    private static String wrap(String table, String column, IDialect dialect) {
        if (StringUtils.isNotBlank(table)) {
            return dialect.wrap(table) + "." + dialect.wrap(column);
        } else {
            return dialect.wrap(column);
        }
    }

    public static String toSelectSql(List<QueryTable> queryTables, QueryColumn queryColumn, IDialect dialect) {
        String tableName = getColumnTableName(queryTables, queryColumn.getTable());
        return wrap(tableName, getColumnName(queryColumn), dialect);
    }

    public static String toConditionSql(List<QueryTable> queryTables, QueryColumn queryColumn, IDialect dialect) {
        String tableName = getColumnTableName(queryTables, queryColumn.getTable());
        return wrap(tableName, getColumnName(queryColumn), dialect);
    }

    public static String toSql(QueryTable queryTable, IDialect dialect) {
        return dialect.wrap(getTableName(queryTable));
    }

    public static String toSql(List<QueryTable> queryTables, QueryCondition condition, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        //检测是否生效
        if (condition.checkEffective()) {
            QueryCondition effectiveBefore = condition.getEffectiveBefore();
            if (effectiveBefore != null) {
                sql.append(effectiveBefore.getConnector());
            }
            // 列
            sql.append(toConditionSql(queryTables, condition.getColumn(), dialect));
            //逻辑符号
            sql.append(getStringCondition(condition.getType()));
            //值（或者问号）
            if (condition.getValue() instanceof QueryColumn) {
                sql.append(toConditionSql(queryTables, ((QueryColumn) condition.getValue()), dialect));
            }
            //子查询
            else if (condition.getValue() instanceof QueryWrapper) {
                sql.append("(").append(dialect.buildSelectSql((QueryWrapper) condition.getValue())).append(")");
            }
            //原生sql
//            else if (value instanceof RawValue) {
//                sql.append(((RawValue) value).getContent());
//            }
            // 正常查询，构建问号
            else {
                appendQuestionMark(condition, sql);
            }
        }

        if (condition.getNext() != null) {
            return sql + toSql(queryTables, condition.getNext(), dialect);
        }

        return sql.toString();
    }

    public static String toSql(List<QueryTable> queryTables, QueryOrderBy orderBy, IDialect dialect) {
        return toConditionSql(queryTables, orderBy.getColumn(), dialect) + " " + orderBy.getType().name();
    }

    public static String toSql(QueryWrapper queryWrapper) {
        return DialectFactory.getDialect().buildSelectSql(queryWrapper);
    }

    private static void appendQuestionMark(QueryCondition condition, StringBuilder sqlBuilder) {
        CONDITION_TYPE type = condition.getType();
        //between, not between
        if (type == CONDITION_TYPE.BETWEEN || type == CONDITION_TYPE.NOT_BETWEEN) {
            sqlBuilder.append(RdsConstant.AND_PLACEHOLDER);
        }
        //in, not in
        else if (type == CONDITION_TYPE.IN_RANGE || type == CONDITION_TYPE.NOT_IN_RANGE) {
            int paramsCount = calculateValueArrayCount(condition);
            sqlBuilder.append(RdsConstant.BRACKET_LEFT);
            for (int i = 0; i < paramsCount; i++) {
                sqlBuilder.append(RdsConstant.PLACEHOLDER);
                if (i != paramsCount - 1) {
                    sqlBuilder.append(RdsConstant.DELIMITER);
                }
            }
            sqlBuilder.append(RdsConstant.BRACKET_RIGHT);
        } else {
            sqlBuilder.append(RdsConstant.PLACEHOLDER);
        }
    }

    private static int calculateValueArrayCount(QueryCondition condition) {
        Object[] values = (Object[]) condition.getValue();
        int paramsCount = 0;
        for (Object object : values) {
            if (object != null && ClassUtils.isArray(object.getClass())) {
                paramsCount += Array.getLength(object);
            } else {
                paramsCount++;
            }
        }
        return paramsCount;
    }

    public static String getStringCondition(CONDITION_TYPE type) {
        String value = "";
        switch (type) {
            case CONTAINS, START_WITH -> {
                value = RdsConstant.LIKE;
            }
            case NOT_CONTAINS -> {
                value = RdsConstant.NOT_LIKE;
            }
            case EQUAL -> {
                value = RdsConstant.EQUALS;
            }
            case NOT_EQUAL -> {
                value = RdsConstant.NOT_EQUALS;
            }
            case LESS -> {
                value = RdsConstant.LT;
            }
            case LESS_EQUAL -> {
                value = RdsConstant.LE;
            }
            case GREATER -> {
                value = RdsConstant.GT;
            }
            case GREATER_EQUAL -> {
                value = RdsConstant.GE;
            }
            case BETWEEN -> {
            }
            case NOT_BETWEEN -> {
            }
            case NOT_START_WITH -> {
            }
            case END_WITH -> {
            }
            case NOT_END_WITH -> {
            }
            case EMPTY -> {
            }
            case NOT_EMPTY -> {
            }
            case IN_RANGE -> {
            }
            case NOT_IN_RANGE -> {
            }
        }
        return value;
    }
}