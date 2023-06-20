package com.pisces.framework.core.dao;

import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.entity.BeanObject;

import java.util.List;

/**
 * 空刀
 *
 * @author jason
 * @date 2022/12/07
 */
public class EmptyDao<T extends BeanObject> implements BaseDao<T> {

    @Override
    public T get() {
        throw new UnsupportedOperationException("get empty dao is not allowed");
    }

    @Override
    public T getById(Long id) {
        throw new UnsupportedOperationException("getById empty dao is not allowed");
    }

    @Override
    public List<T> list() {
        throw new UnsupportedOperationException("list empty dao is not allowed");
    }

    @Override
    public List<T> listByIds(List<Long> ids) {
        throw new UnsupportedOperationException("listByIds empty dao is not allowed");
    }

    @Override
    public boolean exist(Long id) {
        throw new UnsupportedOperationException("exist empty dao is not allowed");
    }

    @Override
    public int insert(T item) {
        throw new UnsupportedOperationException("insert empty dao is not allowed");
    }

    @Override
    public int insertBatch(List<T> items) {
        throw new UnsupportedOperationException("insertBatch empty dao is not allowed");
    }

    @Override
    public int update(T item) {
        throw new UnsupportedOperationException("update empty dao is not allowed");
    }

    @Override
    public int updateBatch(List<T> items) {
        throw new UnsupportedOperationException("updateBatch empty dao is not allowed");
    }

    @Override
    public int delete(T item) {
        throw new UnsupportedOperationException("delete empty dao is not allowed");
    }

    @Override
    public int deleteBatch(List<T> items) {
        throw new UnsupportedOperationException("deleteBatch empty dao is not allowed");
    }

    @Override
    public int deleteById(Long id) {
        throw new UnsupportedOperationException("deleteById empty dao is not allowed");
    }

    @Override
    public int deleteIdBatch(List<Long> ids) {
        throw new UnsupportedOperationException("deleteByIds empty dao is not allowed");
    }

    @Override
    public DaoImpl createDaoImpl() {
        throw new UnsupportedOperationException("empty dao is not allowed");
    }

    @Override
    public void switchDaoImpl(DaoImpl impl) {
        throw new UnsupportedOperationException("empty dao is not allowed");
    }

    @Override
    public void loadData() {
        throw new UnsupportedOperationException("empty dao is not allowed");
    }

    @Override
    public void sync() {
        throw new UnsupportedOperationException("empty dao is not allowed");
    }
}
