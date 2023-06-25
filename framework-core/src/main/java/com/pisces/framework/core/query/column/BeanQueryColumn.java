package com.pisces.framework.core.query.column;

import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.QueryCondition;
import com.pisces.framework.core.query.TableDef;

/**
 * 对象查询列
 *
 * @author jason
 * @date 2023/06/25
 */
public class BeanQueryColumn extends QueryColumn {

    public BeanQueryColumn(TableDef tableDef, String name) {
        super(tableDef, name);
    }

    public QueryCondition empty() {
        return QueryCondition.create(this, CONDITION_TYPE.EMPTY, null);
    }

    public QueryCondition notEmpty() {
        return QueryCondition.create(this, CONDITION_TYPE.NOT_EMPTY, null);
    }
}
