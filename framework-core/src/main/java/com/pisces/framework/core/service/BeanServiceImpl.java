package com.pisces.framework.core.service;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.locale.LanguageService;
import com.pisces.framework.core.query.QueryOrderBy;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.query.condition.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * bean对象服务实现
 *
 * @author jason
 * @date 2023/06/27
 */
public class BeanServiceImpl<T extends BeanObject, D extends BaseDao<T>> extends BaseServiceImpl implements BeanService<T> {
    private final Class<T> beanClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @Autowired
    protected LanguageService lang;
    @Autowired
    private D dao;

    public BeanServiceImpl() {
        ServiceManager.register(getBeanClass(), this);
    }

    protected D getDao() {
        return dao;
    }

    @Override
    public Class<T> getBeanClass() {
        return beanClass;
    }

    @Override
    public BaseDao<T> getBaseDao() {
        return getDao();
    }

    @Override
    public T create() {
        return getDao().create(getBeanClass());
    }

    @Override
    public T get() {
        return getDao().get();
    }

    @Override
    public T getById(long id) {
        return getDao().getById(id);
    }

    @Override
    public T get(QueryCondition condition) {
        QueryWrapper qw = QueryWrapper.from(getBeanClass()).where(condition);
        return getDao().fetchOne(qw);
    }

    @Override
    public List<T> list() {
        return getDao().list();
    }

    @Override
    public List<T> listByIds(List<Long> ids) {
        return getDao().listByIds(ids);
    }

    @Override
    public List<T> list(QueryCondition condition, QueryOrderBy... orderBys) {
        QueryWrapper qw = QueryWrapper.from(getBeanClass()).where(condition).orderBy(orderBys);
        return getDao().fetch(qw);
    }

    @Override
    public boolean exist(long id) {
        return getDao().exist(id);
    }

    @Override
    public int insert(T item) {
        return getDao().insert(item);
    }

    @Override
    public int insertBatch(List<T> items) {
        return getDao().insertBatch(items);
    }

    @Override
    public int update(T item) {
        return getDao().update(item);
    }

    @Override
    public int updateBatch(List<T> items) {
        return getDao().updateBatch(items);
    }

    @Override
    public int delete(T item) {
        return getDao().delete(item);
    }

    @Override
    public int deleteBatch(List<T> items) {
        return getDao().deleteBatch(items);
    }

    @Override
    public int deleteById(long id) {
        return getDao().deleteById(id);
    }

    @Override
    public int deleteIdBatch(List<Long> ids) {
        return getDao().deleteIdBatch(ids);
    }

    @Override
    public void delete(QueryCondition condition) {

    }
}
