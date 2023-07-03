package com.pisces.framework.rds.provider.resolve;

import com.pisces.framework.rds.helper.entity.Config;
import com.pisces.framework.rds.helper.entity.EntityTable;

/**
 * 解析实体类接口
 *
 * @author jason
 * @date 2023/07/02
 */
public interface EntityResolve {

    /**
     * 解析类为 EntityTable
     *
     * @param entityClass 实体类
     * @param config      配置
     * @return {@link EntityTable}
     */
    EntityTable resolveEntity(Class<?> entityClass, Config config);
}
