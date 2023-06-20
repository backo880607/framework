package com.pisces.framework.rds.common;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.dao.DaoManager;
import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.dao.impl.SingletonModifyDaoImpl;
import com.pisces.framework.core.entity.EntityAccount;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 只和账号相关不与数据集相关
 *
 * @param <T>
 * @author jason
 * @date 2022/12/07
 */
public class SQLAccountDao<T extends EntityAccount> extends SqlSessionDaoSupport implements BaseDao<T> {
    private final Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    private RdsMapper<T> mapper;

    public SQLAccountDao() {
    }

    @Override
    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }

    @Override
    protected void initDao() throws Exception {
        DaoManager.register(this);
    }

    private T create() {
        T entity = BeanUtils.instantiateClass(entityClass);
        entity.init();
//            entity.setDataSetId(AppUtils.getDataSetId());
        return entity;
    }

    @Override
    public T get() {
        return null;
//        Example example = new Example(entityClass);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("account", AppUtils.getUsername());
//        T item = mapper.selectOneByExample(example);
//        if (item == null) {
//            item = create();
//            item.setAccount(AppUtils.getUsername());
//            mapper.insert(item);
//        }
//        return item;
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

    @Override
    public DaoImpl createDaoImpl() {
        return new SingletonModifyDaoImpl<>();
    }

    @Override
    public void switchDaoImpl(DaoImpl impl) {
    }

    @Override
    public void loadData() {
    }

    @Override
    public void sync() {
    }
}
