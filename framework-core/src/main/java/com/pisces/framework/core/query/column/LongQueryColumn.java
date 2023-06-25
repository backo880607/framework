package com.pisces.framework.core.query.column;

import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.QueryCondition;
import com.pisces.framework.core.query.TableDef;

import java.util.Collection;

/**
 * 长整型查询列
 *
 * @author jason
 * @date 2023/06/25
 */
public class LongQueryColumn extends QueryColumn {

    public LongQueryColumn(TableDef tableDef, String name) {
        super(tableDef, name);
    }

    public QueryCondition equal(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.EQUAL, value);
    }

    public QueryCondition notEqual(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.NOT_EQUAL, value);
    }

    public QueryCondition greater(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.GREATER, value);
    }

    public QueryCondition greaterEqual(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.GREATER_EQUAL, value);
    }

    public QueryCondition less(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.LESS, value);
    }

    public QueryCondition lessEqual(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.LESS_EQUAL, value);
    }

    public QueryCondition empty() {
        return QueryCondition.create(this, CONDITION_TYPE.EMPTY, null);
    }

    public QueryCondition notEmpty() {
        return QueryCondition.create(this, CONDITION_TYPE.NOT_EMPTY, null);
    }

    public QueryCondition in(Object... arrays) {
        //忽略 QueryWrapper.in("name", null) 的情况
        if (arrays == null || arrays.length == 0 || (arrays.length == 1 && arrays[0] == null)) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.IN_RANGE, arrays);
    }

    public QueryCondition in(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return in(collection.toArray());
        }
        return QueryCondition.createEmpty();
    }

    public QueryCondition notIn(Object... arrays) {
        //忽略 QueryWrapper.notIn("name", null) 的情况
        if (arrays == null || arrays.length == 0 || (arrays.length == 1 && arrays[0] == null)) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, CONDITION_TYPE.NOT_IN_RANGE, arrays);
    }

    public QueryCondition notIn(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return notIn(collection.toArray());
        }
        return QueryCondition.createEmpty();
    }

    public QueryCondition between(Object start, Object end) {
        return QueryCondition.create(this, CONDITION_TYPE.BETWEEN, new Object[]{start, end});
    }

    public QueryCondition notBetween(Object start, Object end) {
        return QueryCondition.create(this, CONDITION_TYPE.NOT_BETWEEN, new Object[]{start, end});
    }
}
