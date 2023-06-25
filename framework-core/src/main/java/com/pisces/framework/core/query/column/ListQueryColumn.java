package com.pisces.framework.core.query.column;

import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.QueryCondition;
import com.pisces.framework.core.query.TableDef;

/**
 * 对象集合查询列
 *
 * @author jason
 * @date 2023/06/25
 */
public class ListQueryColumn extends QueryColumn {

    public ListQueryColumn(TableDef tableDef, String name) {
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

    public QueryCondition empty() {
        return QueryCondition.create(this, CONDITION_TYPE.EMPTY, null);
    }

    public QueryCondition notEmpty() {
        return QueryCondition.create(this, CONDITION_TYPE.NOT_EMPTY, null);
    }
}
