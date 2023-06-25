package com.pisces.framework.core.query;

import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 查询包装器
 *
 * @author jason
 * @date 2023/06/25
 */
public class QueryWrapper extends BaseQueryWrapper<QueryWrapper> {

    public static QueryWrapper from(TableDef... tableDefs) {
        QueryWrapper query = new QueryWrapper();
        for (TableDef tableDef : tableDefs) {
            query.from(new QueryTable(tableDef.getTableName()));
        }
        return query;
    }

    public QueryWrapper from(String... tables) {
        for (String table : tables) {
            if (StringUtils.isBlank(table)) {
                throw new IllegalArgumentException("table must not be null or blank.");
            }
            from(new QueryTable(table));
        }
        return this;
    }

    public QueryWrapper from(QueryTable... tables) {
        if (CollectionUtils.isEmpty(queryTables)) {
            queryTables = new ArrayList<>();
            queryTables.addAll(Arrays.asList(tables));
        } else {
            for (QueryTable table : tables) {
                boolean contains = false;
                for (QueryTable queryTable : queryTables) {
                    if (queryTable.isSameTable(table)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    queryTables.add(table);
                }
            }
        }
        return this;
    }

    public QueryWrapper where(QueryCondition queryCondition) {
        this.setWhereQueryCondition(queryCondition);
        return this;
    }

    public QueryWrapper and(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, QueryConnector.AND);
    }

    public QueryWrapper or(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, QueryConnector.OR);
    }

    public QueryWrapper groupBy(QueryColumn... columns) {
        for (QueryColumn column : columns) {
            groupBy(column);
        }
        return this;
    }
    
    public QueryWrapper having(QueryCondition queryCondition) {
        addHavingQueryCondition(queryCondition, QueryConnector.AND);
        return this;
    }

    public QueryWrapper orderBy(QueryOrderBy... orderBys) {
        for (QueryOrderBy queryOrderBy : orderBys) {
            addOrderBy(queryOrderBy);
        }
        return this;
    }

    public QueryWrapper limit(Integer rows) {
        setLimitRows(rows);
        return this;
    }

    public QueryWrapper offset(Integer offset) {
        setLimitOffset(offset);
        return this;
    }

    public QueryWrapper limit(Integer offset, Integer rows) {
        setLimitOffset(offset);
        setLimitRows(rows);
        return this;
    }

    public QueryWrapper select(QueryColumn... queryColumns) {
        for (QueryColumn column : queryColumns) {
            if (column != null) {
                addSelectColumn(column);
            }
        }
        return this;
    }

    public Object[] getValueArray() {
        return new Object[0];
    }
}
