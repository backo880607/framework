package com.pisces.framework.rds.common;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BaseObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * sqldao
 *
 * @author jason
 * @date 2022/12/07
 */
public class SQLDao<T extends BaseObject> extends SqlSessionDaoSupport implements BaseDao<T> {
    private final Class<T> objectClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    private RdsMapper<T> mapper;

    @Override
    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }

    @Override
    public Class<T> getObjectClass() {
        return this.objectClass;
    }

    @Override
    public T get() {
        throw new UnsupportedOperationException("select one is not allowed");
    }

    @Override
    public T getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<T> list() {
        return mapper.selectAll();
    }

    @Override
    public List<T> listByIds(Collection<Long> ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public boolean exist(Long id) {
        return mapper.existsById(id);
    }

    @Override
    public int insert(T item) {
        return mapper.insert(item);
    }

    @Override
    public int insertBatch(List<T> items) {
        return mapper.insertBatch(items, 0);
    }

    @Override
    public int update(T item) {
        return mapper.update(item);
    }

    @Override
    public int updateBatch(List<T> items) {
        return mapper.updateBatch(items, 0);
    }

    @Override
    public int delete(T item) {
        return mapper.deleteById(item.getId());
    }

    @Override
    public int deleteBatch(List<T> items) {
        return mapper.deleteBatch(items, 0);
    }

    @Override
    public int deleteById(Long id) {
        return mapper.deleteById(id);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return mapper.deleteBatchByIds(ids, 0);
    }
}