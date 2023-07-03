package com.pisces.framework.rds.common;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.dao.impl.MemoryModifyDaoImpl;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.table.QBeanObject;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.rds.query.SqlExecutor;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * sql dao
 *
 * @author jason
 * @date 2022/12/07
 */
public class SQLDao<T extends BeanObject> extends SqlSessionDaoSupport implements BaseDao<T> {
    private final Class<T> beanClass = getBeanClass();

    @Autowired
    private RdsMapper<T> mapper;

    @Override
    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }

    @Override
    protected void initDao() {
        SQLDaoManager.register(this);
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
    public T fetchOne(QueryWrapper qw) {
        qw.and(QBeanObject.dataSetId.bind(beanClass).equal(0));
        return SqlExecutor.fetchOne(qw, beanClass);
    }

    @Override
    public List<T> list() {
        return mapper.selectAll();
    }

    @Override
    public List<T> listByIds(List<Long> ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> fetch(QueryWrapper qw) {
        qw.and(QBeanObject.dataSetId.bind(beanClass).equal(0));
        return SqlExecutor.fetch(qw, beanClass);
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
    public int deleteIdBatch(List<Long> ids) {
        return mapper.deleteBatchByIds(ids, 0);
    }

    @Override
    public DaoImpl createDaoImpl() {
        return new MemoryModifyDaoImpl<T>();
    }

    @Override
    public void switchDaoImpl(DaoImpl impl) {
    }
}