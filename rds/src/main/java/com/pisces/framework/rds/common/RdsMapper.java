package com.pisces.framework.rds.common;

import com.pisces.framework.rds.annotation.RegisterMapper;
import com.pisces.framework.rds.provider.RdsProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * rds映射器
 *
 * @author jason
 * @date 2022/12/07
 */
@RegisterMapper
public interface RdsMapper<T> {
    /**
     * 检查表
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    void checkTable();

    /**
     * 根据id进行查询
     *
     * @param id id
     * @return {@link T}
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    T selectById(Long id);

    /**
     * 保存一个实体，null的属性不会被保存, 会使用数据库默认值
     *
     * @param record 记录
     * @return int
     */
    @InsertProvider(type = RdsProvider.class, method = "dynamicSQL")
    int insert(T record);
}
