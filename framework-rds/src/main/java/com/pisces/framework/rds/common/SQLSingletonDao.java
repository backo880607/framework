package com.pisces.framework.rds.common;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.rds.query.SqlExecutor;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * sqlsingleton刀
 *
 * @author jason
 * @date 2022/12/07
 */
public class SQLSingletonDao<T extends BeanObject> extends SqlSessionDaoSupport implements BaseDao<T> {
    @Autowired
    private RdsMapper<T> mapper;

    public SQLSingletonDao() {
    }

    @Override
    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }

    @Override
    protected void initDao() throws Exception {
        SQLDaoManager.register(this);
    }

    @Override
    public T get() {
        Class<T> beanClass = getBeanClass();
        QueryWrapper qw = QueryWrapper.from(beanClass);
        return SqlExecutor.fetchOne(qw, beanClass);
    }

    @Override
    public List<T> list() {
        ArrayList<T> result = new ArrayList<>();
        result.add(get());
        return result;
    }

    @Override
    public T getById(Long id) {
        throw new UnsupportedOperationException("selectByPrimaryKey Singleton bean is not allowed");
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
    public int update(T record) {
        mapper.update(record);
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
}
