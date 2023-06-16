package com.pisces.framework.core.service;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.locale.LanguageService;
import com.pisces.framework.core.locale.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 基础服务impl
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class BaseServiceImpl<T extends BaseObject, D extends BaseDao<T>> implements BaseService<T> {
    protected final Message LOG = new Message(this.getClass());
    private final Class<T> objectClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @Autowired
    protected LanguageService lang;
    @Autowired
    private D dao;

    public BaseServiceImpl() {
        ServiceManager.register(objectClass, this);
    }

    protected D getDao() { return dao; }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public BaseDao<T> getBaseDao() {
        return getDao();
    }

    @Override
    public T create() {
        T entity = BeanUtils.instantiateClass(getObjectClass());
        entity.init();
        return entity;
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
    public List<T> list() {
        return getDao().list();
    }

    @Override
    public List<T> listByIds(List<Long> ids) {
        return getDao().listByIds(ids);
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
    public void clear() {

    }
}
