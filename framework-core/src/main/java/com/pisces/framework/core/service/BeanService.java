package com.pisces.framework.core.service;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BeanObject;

import java.util.List;

public interface BeanService<T extends BeanObject> extends BaseService {
    /**
     * 得到对象类
     *
     * @return {@link Class}<{@link T}>
     */
    Class<T> getBeanClass();

    /**
     * 获取对象Dao
     *
     * @return {@link BaseDao}<{@link T}>
     */
    BaseDao<T> getBaseDao();

    /**
     * 创建实体对象
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
     * @param item 实体
     * @return int
     */
    int insert(T item);

    /**
     * 插入列表
     *
     * @param items 实体
     * @return int
     */
    int insertBatch(List<T> items);

    /**
     * 更新
     *
     * @param item 实体
     * @return int
     */
    int update(T item);

    /**
     * 更新列表
     *
     * @param items 实体
     * @return int
     */
    int updateBatch(List<T> items);

    /**
     * 删除
     *
     * @param item 实体
     * @return int
     */
    int delete(T item);

    /**
     * 删除表
     *
     * @param items 实体
     * @return int
     */
    int deleteBatch(List<T> items);

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
    int deleteIdBatch(List<Long> ids);

    /**
     * 清晰
     */
    void clear();
}
