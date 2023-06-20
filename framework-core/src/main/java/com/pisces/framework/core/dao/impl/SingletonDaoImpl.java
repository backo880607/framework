package com.pisces.framework.core.dao.impl;

import com.pisces.framework.core.entity.BeanObject;

/**
 * 单道impl
 *
 * @author jason
 * @date 2022/12/07
 */
public class SingletonDaoImpl<T extends BeanObject> implements DaoImpl {
    private T record;

    public T getRecord() {
        return record;
    }

    public void setRecord(T record) {
        this.record = record;
    }
}
