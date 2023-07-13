package com.pisces.framework.core.dao;

import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.PageParam;
import com.pisces.framework.core.query.QueryOrderBy;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.query.condition.QueryCondition;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Dao基类
 *
 * @author jason
 * @date 2022/12/07
 */
public interface BaseDao<T extends BeanObject> {

    default Class<T> getBeanClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    default T create(Class<T> beanClass) {
        T bean = BeanUtils.instantiateClass(beanClass);
        bean.init();
        return bean;
    }

    /**
     * select one entity from dao.
     *
     * @return entity
     */
    T get();

    /**
     * select one entity by id from dao.
     *
     * @param id entity`s id
     * @return entity
     */
    T getById(Long id);

    /**
     * 得到
     *
     * @param condition 条件
     * @return {@link T}
     */
    default T get(QueryCondition condition) {
        QueryWrapper qw = QueryWrapper.from(getBeanClass()).where(condition);
        return fetchOne(qw);
    }

    /**
     * 获取一个
     *
     * @param qw qw
     * @return {@link T}
     */
    default T fetchOne(QueryWrapper qw) {
        throw new UnsupportedOperationException("fetchOne is not allowed");
    }

    /**
     * 选择所有
     *
     * @return {@link List}<{@link T}>
     */
    List<T> list();

    /**
     * 通过id
     *
     * @param ids id列表
     * @return {@link List}<{@link T}>
     */
    List<T> listByIds(List<Long> ids);

    /**
     * 列表
     *
     * @param condition 条件
     * @param orderBys  订单
     * @return {@link List}<{@link T}>
     */
    default List<T> list(QueryCondition condition, QueryOrderBy... orderBys) {
        QueryWrapper qw = QueryWrapper.from(getBeanClass()).where(condition).orderBy(orderBys);
        return fetch(qw);
    }

    default List<T> list(PageParam param) {
        QueryWrapper qw = QueryWrapper.from(getBeanClass());
//        if (StringUtils.isNotBlank(param.getFilter())) {
//            qw.where(param.getFilter().trim());
//        }
        return fetch(qw);
    }

    /**
     * 获取
     *
     * @param qw qw
     * @return {@link List}<{@link T}>
     */
    default List<T> fetch(QueryWrapper qw) {
        throw new UnsupportedOperationException("fetchOne is not allowed");
    }

    /**
     * 存在
     *
     * @param id id
     * @return boolean
     */
    boolean exist(Long id);

    /**
     * 插入
     *
     * @param item 记录
     * @return int
     */
    int insert(T item);

    /**
     * 插入列表
     *
     * @param items 记录列表
     * @return int
     */
    int insertBatch(List<T> items);

    /**
     * 更新
     *
     * @param item 记录
     * @return int
     */
    int update(T item);

    /**
     * 更新列表
     *
     * @param items 记录列表
     * @return int
     */
    int updateBatch(List<T> items);

    /**
     * 删除
     *
     * @param item 记录
     * @return int
     */
    int delete(T item);

    /**
     * 删除表
     *
     * @param items 记录列表
     * @return int
     */
    int deleteBatch(List<T> items);

    /**
     * 删除通过id
     *
     * @param id id
     * @return int
     */
    int deleteById(Long id);

    /**
     * 删除由ids
     *
     * @param ids id列表
     * @return int
     */
    int deleteIdBatch(List<Long> ids);

    /**
     * 创建刀impl
     *
     * @return {@link DaoImpl}
     */
    DaoImpl createDaoImpl();

    /**
     * 换刀impl
     *
     * @param impl impl
     */
    void switchDaoImpl(DaoImpl impl);
}
