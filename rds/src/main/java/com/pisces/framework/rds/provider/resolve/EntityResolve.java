package com.pisces.framework.rds.provider.resolve;

import com.pisces.framework.rds.entity.Config;
import com.pisces.framework.rds.entity.EntityTable;

/**
 * 解析实体类接口
 *
 * @author liuzh
 */
public interface EntityResolve {

    /**
     * 解析类为 EntityTable
     *
     * @param entityClass
     * @param config
     * @return
     */
    EntityTable resolveEntity(Class<?> entityClass, Config config);
}
