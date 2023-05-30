package com.pisces.framework.rds.mapper;

import com.pisces.framework.rds.annotation.RegisterMapper;
import com.pisces.framework.rds.provider.CustomizeProvider;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 自定义映射器
 *
 * @author jason
 * @date 2022/12/07
 */
@RegisterMapper
public interface CustomizeMapper<T> {

    /**
     * 检查表
     */
    @SelectProvider(type = CustomizeProvider.class, method = "dynamicSQL")
    void checkTable();
}
