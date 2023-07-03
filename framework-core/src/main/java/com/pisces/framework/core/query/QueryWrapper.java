package com.pisces.framework.core.query;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.query.condition.QueryConnector;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 查询包装器
 *
 * @author jason
 * @date 2023/06/25
 */
public class QueryWrapper extends BaseQueryWrapper {

    public static QueryWrapper from(String... tables) {
        QueryWrapper query = new QueryWrapper();
        for (String table : tables) {
            Class<? extends BeanObject> beanClass = ObjectUtils.fetchBeanClass(table);
            query.from(new QueryTable(beanClass));
        }
        return query;
    }

    @SafeVarargs
    public static QueryWrapper from(Class<? extends BeanObject>... beanClasses) {
        QueryWrapper query = new QueryWrapper();
        for (Class<? extends BeanObject> beanClass : beanClasses) {
            query.from(new QueryTable(beanClass));
        }
        return query;
    }

    public QueryWrapper from(QueryTable... tables) {
        if (CollectionUtils.isEmpty(queryTables)) {
            queryTables = new ArrayList<>(Arrays.asList(tables));
        } else {
            for (QueryTable table : tables) {
                boolean contains = false;
                for (QueryTable queryTable : queryTables) {
                    if (queryTable.isSame(table)) {
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
        addWhereQueryCondition(queryCondition, QueryConnector.AND);
        return this;
    }

    public QueryWrapper or(QueryCondition queryCondition) {
        addWhereQueryCondition(queryCondition, QueryConnector.OR);
        return this;
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
