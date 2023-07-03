package com.pisces.framework.core.query.column;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.condition.QueryCondition;

/**
 * 日期查询列
 *
 * @author jason
 * @date 2023/06/25
 */
public class DateQueryColumn extends QueryColumn {

    public DateQueryColumn(String beanName, String fieldName) {
        super(beanName, fieldName);
    }

    public DateQueryColumn bind(Class<? extends BeanObject> beanClass) {
        return new DateQueryColumn(beanClass.getSimpleName(), getName());
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

    public QueryCondition between(Object start, Object end) {
        return QueryCondition.create(this, CONDITION_TYPE.BETWEEN, new Object[]{start, end});
    }

    public QueryCondition notBetween(Object start, Object end) {
        return QueryCondition.create(this, CONDITION_TYPE.NOT_BETWEEN, new Object[]{start, end});
    }
}
