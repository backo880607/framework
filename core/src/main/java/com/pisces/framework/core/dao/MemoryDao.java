package com.pisces.framework.core.dao;

import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.dao.impl.MemoryDaoImpl;
import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.utils.lang.EntityUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 记忆刀
 *
 * @author jason
 * @date 2022/12/07
 */
public class MemoryDao<T extends BaseObject> implements BaseDao<T> {
    private final ThreadLocal<MemoryDaoImpl<T>> impl = new ThreadLocal<>();

    public MemoryDao() {
        DaoManager.register(this);
    }

    private Class<T> getObjectClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private T create() {
        T item = BeanUtils.instantiateClass(getObjectClass());
        item.init();
        return item;
    }

    @Override
    public T get() {
        Iterator<Entry<Long, T>> iterator = impl.get().getRecords().entrySet().iterator();
        return iterator.hasNext() ? iterator.next().getValue() : null;
    }

    @Override
    public T getById(Long id) {
        return impl.get().getRecords().get(id);
    }

    @Override
    public List<T> list() {
        return new ArrayList<>(impl.get().getRecords().values());
    }

    @Override
    public List<T> listByIds(Collection<Long> idList) {
        List<T> result = new ArrayList<>();
        for (Long id : idList) {
            T item = this.getById(id);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public boolean exist(Long id) {
        return impl.get().getRecords().containsKey(id);
    }

    @Override
    public int insert(T item) {
        if (!item.isInitialized()) {
            T newItem = create();
            EntityUtils.copyIgnoreNull(item, newItem);
            item = newItem;
        }
        impl.get().getRecords().put(item.getId(), item);
        return 1;
    }

    @Override
    public int insertBatch(List<T> items) {
        for (T item : items) {
            insert(item);
        }
        return items.size();
    }

    @Override
    public int update(T item) {
        T oldItem = getById(item.getId());
        if (oldItem == null) {
            throw new IllegalArgumentException("invalid bean id: " + item.getId());
        }

        if (oldItem != item) {
            EntityUtils.copyIgnoreNull(item, oldItem);
        }
        return 1;
    }

    @Override
    public int updateBatch(List<T> items) {
        for (T item : items) {
            update(item);
        }
        return items.size();
    }

    @Override
    public int delete(T item) {
        return deleteById(item.getId());
    }

    @Override
    public int deleteBatch(List<T> items) {
        int count = 0;
        for (T item : items) {
            count += delete(item);
        }
        return count;
    }

    @Override
    public int deleteById(Long id) {
        return impl.get().getRecords().remove(id) != null ? 1 : 0;
    }

    @Override
    public int deleteIdBatch(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            count += deleteById(id);
        }
        return count;
    }

    @Override
    public void loadData() {
    }

    @Override
    public void sync() {
    }

    @Override
    public DaoImpl createDaoImpl() {
        return new MemoryDaoImpl<T>();
    }

    @Override
    public void switchDaoImpl(DaoImpl impl) {
        this.impl.remove();
        this.impl.set((MemoryDaoImpl<T>) impl);
    }
}
