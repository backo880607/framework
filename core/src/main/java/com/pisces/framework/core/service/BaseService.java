package com.pisces.framework.core.service;

import com.pisces.framework.core.entity.BaseObject;

import java.util.List;

/**
 * 基础服务
 *
 * @author jason
 * @date 2022/12/07
 */
public interface BaseService<T extends BaseObject> {

    /**
     * 得到对象类
     *
     * @return {@link Class}<{@link T}>
     */
    Class<T> getObjectClass();

    /**
     * 创建
     *
     * @return {@link T}
     */
    T create();

    /**
     * 得到
     *
     * @return {@link T}
     */
    T get();

    /**
     * 通过id
     *
     * @param id id
     * @return {@link T}
     */
    T getById(long id);

    /**
     * 列表
     *
     * @return {@link List}<{@link T}>
     */
    List<T> list();

    /**
     * 通过id
     *
     * @param ids id
     * @return {@link List}<{@link T}>
     */
    List<T> listByIds(List<Long> ids);

    /**
     * 存在
     *
     * @param id id
     * @return boolean
     */
    boolean exist(long id);

    /**
     * 插入
     *
     * @param entity 实体
     * @return int
     */
    int insert(T entity);

    /**
     * 插入列表
     *
     * @param entities 实体
     * @return int
     */
    int insertBatch(List<T> entities);

    /**
     * 更新
     *
     * @param entity 实体
     * @return int
     */
    int update(T entity);

    /**
     * 更新列表
     *
     * @param entities 实体
     * @return int
     */
    int updateBatch(List<T> entities);

    /**
     * 删除
     *
     * @param entity 实体
     * @return int
     */
    int delete(T entity);

    /**
     * 删除表
     *
     * @param entities 实体
     * @return int
     */
    int deleteBatch(List<T> entities);

    /**
     * 删除通过id
     *
     * @param id id
     * @return int
     */
    int deleteById(long id);

    /**
     * 删除由ids
     *
     * @param ids id
     * @return int
     */
    int deleteByIds(List<Long> ids);

    /**
     * 清晰
     */
    void clear();
}
