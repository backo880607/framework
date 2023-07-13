package com.pisces.framework.core.service;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.PageParam;
import com.pisces.framework.core.query.QueryOrderBy;
import com.pisces.framework.core.query.condition.QueryCondition;

import java.util.List;

/**
 * bean对象服务接口
 *
 * @author jason
 * @date 2023/06/27
 */
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
     * 得到
     *
     * @param condition 查询条件
     * @return {@link T}
     */
    T get(QueryCondition condition);

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
     * 列表
     *
     * @param condition 查询条件
     * @param orderBys  订单
     * @return {@link List}<{@link T}>
     */
    List<T> list(QueryCondition condition, QueryOrderBy... orderBys);

    /**
     * @param param 查询参数
     * @return {@link List}<{@link T}>
     */
    List<T> list(PageParam param);

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
     * 删除
     *
     * @param condition 条件
     */
    void delete(QueryCondition condition);
}
