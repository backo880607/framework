package com.pisces.framework.core.query.column;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.condition.QueryCondition;

/**
 * 布尔查询列
 *
 * @author jason
 * @date 2023/06/25
 */
public class BooleanQueryColumn extends QueryColumn {

    public BooleanQueryColumn(String beanName, String fieldName) {
        super(beanName, fieldName);
    }

    public BooleanQueryColumn bind(Class<? extends BeanObject> beanClass) {
        return new BooleanQueryColumn(beanClass.getSimpleName(), getName());
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
