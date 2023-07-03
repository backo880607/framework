package com.pisces.framework.core.dao;

import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.dao.impl.SingletonDaoImpl;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import org.springframework.beans.BeanUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 单道
 *
 * @author jason
 * @date 2022/12/07
 */
public class SingletonDao<T extends BeanObject> implements BaseDao<T> {
    private final Class<T> beanClass = getBeanClass();
    private final ThreadLocal<SingletonDaoImpl<T>> impl = new ThreadLocal<>();

    public SingletonDao() {
        DaoManager.register(this);
    }

    @Override
    public T get() {
        return impl.get().getRecord();
    }

    @Override
    public T getById(Long id) {
        throw new UnsupportedOperationException("selectByPrimaryKey Singleton bean is not allowed");
    }

    @Override
    public List<T> list() {
        List<T> result = new LinkedList<>();
        result.add(impl.get().getRecord());
        return result;
    }

    @Override
    public List<T> listByIds(List<Long> ids) {
        throw new UnsupportedOperationException("selectByIds Singleton bean is not allowed");
    }

    @Override
    public boolean exist(Long id) {
        throw new UnsupportedOperationException("existsWithPrimaryKey Singleton bean is not allowed");
    }

    @Override
    public int insert(T record) {
        throw new UnsupportedOperationException("insert Singleton bean is not allowed");
    }

    @Override
    public int insertBatch(List<T> items) {
        throw new UnsupportedOperationException("insert Singleton bean is not allowed");
    }

    @Override
    public int update(T item) {
        T oldItem = get();
        if (oldItem != item) {
            ObjectUtils.copyIgnoreNull(item, oldItem);
        }
        return 1;
    }

    @Override
    public int updateBatch(List<T> items) {
        if (items.isEmpty()) {
            return 0;
        }
        return update(items.iterator().next());
    }

    @Override
    public int delete(T record) {
        throw new UnsupportedOperationException("delete Singleton bean is not allowed");
    }

    @Override
    public int deleteBatch(List<T> items) {
        throw new UnsupportedOperationException("delete Singleton bean is not allowed");
    }

    @Override
    public int deleteById(Long id) {
        throw new UnsupportedOperationException("delete Singleton bean is not allowed");
    }

    @Override
    public int deleteIdBatch(List<Long> ids) {
        throw new UnsupportedOperationException("delete Singleton bean is not allowed");
    }

    @Override
    public DaoImpl createDaoImpl() {
        return new SingletonDaoImpl<T>();
    }

    @Override
    public void switchDaoImpl(DaoImpl value) {
        impl.remove();
        this.impl.set((SingletonDaoImpl<T>) value);
        this.impl.get().setRecord(BeanUtils.instantiateClass(beanClass));
        this.impl.get().getRecord().init();
    }

}
