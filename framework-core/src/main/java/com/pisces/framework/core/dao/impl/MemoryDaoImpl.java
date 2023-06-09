package com.pisces.framework.core.dao.impl;

import com.pisces.framework.core.entity.BeanObject;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记忆刀impl
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class MemoryDaoImpl<T extends BeanObject> implements DaoImpl {
    private final Map<Long, T> records = new ConcurrentHashMap<>();
}
