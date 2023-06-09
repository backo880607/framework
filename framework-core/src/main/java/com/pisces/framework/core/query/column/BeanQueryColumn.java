package com.pisces.framework.core.query.column;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.condition.QueryCondition;

/**
 * 对象查询列
 *
 * @author jason
 * @date 2023/06/25
 */
public class BeanQueryColumn extends QueryColumn {

    public BeanQueryColumn(String beanName, String fieldName) {
        super(beanName, fieldName);
    }

    public BeanQueryColumn bind(Class<? extends BeanObject> beanClass) {
        return new BeanQueryColumn(beanClass.getSimpleName(), getName());
    }

    public QueryCondition empty() {
        return QueryCondition.create(this, CONDITION_TYPE.EMPTY, null);
    }

    public QueryCondition notEmpty() {
        return QueryCondition.create(this, CONDITION_TYPE.NOT_EMPTY, null);
    }
}
