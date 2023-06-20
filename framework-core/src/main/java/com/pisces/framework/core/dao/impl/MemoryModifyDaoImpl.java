package com.pisces.framework.core.dao.impl;

import com.pisces.framework.core.entity.BeanObject;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 内存修改刀impl
 *
 * @author jason
 * @date 2022/12/07
 */
public class MemoryModifyDaoImpl<T extends BeanObject> extends MemoryDaoImpl<T> {
    private final Collection<Long> deleted = new ConcurrentLinkedQueue<>();

    public Collection<Long> getDeleted() {
        return deleted;
    }
}
