package com.pisces.framework.core.dao;

import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

/**
 * 全球刀
 *
 * @author jason
 * @date 2022/12/07
 */
public class GlobalDao<T extends BeanObject> implements BaseDao<T> {
    private final T record;

    public GlobalDao() {
        Class<T> objectClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        record = BeanUtils.instantiateClass(objectClass);
        record.init();
    }

    @Override
    public T get() {
        return this.record;
    }

    @Override
    public T getById(Long id) {
        return this.record;
    }

    @Override
    public List<T> list() {
        List<T> result = new LinkedList<>();
        result.add(this.record);
        return result;
    }

    @Override
    public List<T> listByIds(List<Long> ids) {
        return list();
    }

    @Override
    public boolean exist(Long id) {
        return true;
    }

    @Override
    public int insert(T item) {
        throw new UnsupportedOperationException("insert global bean is not allowed");
    }

    @Override
    public int insertBatch(List<T> items) {
        throw new UnsupportedOperationException("insertBatch global bean is not allowed");
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
    public int delete(T item) {
        throw new UnsupportedOperationException("delete global bean is not allowed");
    }

    @Override
    public int deleteBatch(List<T> items) {
        throw new UnsupportedOperationException("deleteBatch global bean is not allowed");
    }

    @Override
    public int deleteById(Long id) {
        throw new UnsupportedOperationException("delete global bean is not allowed");
    }

    @Override
    public int deleteIdBatch(List<Long> ids) {
        throw new UnsupportedOperationException("deleteByIds global bean is not allowed");
    }

    @Override
    public void loadData() {
        throw new UnsupportedOperationException("global dao is not allowed");
    }

    @Override
    public void sync() {
        throw new UnsupportedOperationException("global dao is not allowed");
    }

    @Override
    public DaoImpl createDaoImpl() {
        throw new UnsupportedOperationException("global dao is not allowed");
    }

    @Override
    public void switchDaoImpl(DaoImpl impl) {
        throw new UnsupportedOperationException("global dao is not allowed");
    }
}
