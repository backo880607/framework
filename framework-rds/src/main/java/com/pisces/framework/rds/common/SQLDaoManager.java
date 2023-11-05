package com.pisces.framework.rds.common;

import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.exception.SystemException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * sqldao经理
 *
 * @author jason
 * @date 2023/06/29
 */
public class SQLDaoManager {
    private static final Set<Class<? extends BeanObject>> DAO_CLASS = new HashSet<>();

    public static void register(BaseDao<? extends BeanObject> dao) {
        DAO_CLASS.add(dao.getBeanClass());
    }

    public static void checkSupport(List<Class<? extends BeanObject>> beanClasses) {
        for (Class<? extends BeanObject> beanClass : beanClasses) {
            if (!DAO_CLASS.contains(beanClass)) {
                throw new SystemException(beanClass.getName() + " has not bind a service!");
            }
        }
    }
}
