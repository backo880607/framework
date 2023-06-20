package com.pisces.framework.core.dao;

import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 道经理
 *
 * @author jason
 * @date 2022/12/07
 */
public class DaoManager {
    private static final Map<String, UserData> USER_DATA_LIST = new ConcurrentHashMap<>();
    private static final List<BaseDao<? extends BeanObject>> DAO_LIST = new ArrayList<>();
    private static final List<BaseDao<? extends BeanObject>> MEMORY_DAO = new ArrayList<>();

    protected DaoManager() {
    }

    public static void register(BaseDao<? extends BeanObject> dao) {
        DAO_LIST.add(dao);
    }

    public static void registerMemory(BaseDao<? extends BeanObject> dao) {
        register(dao);
        MEMORY_DAO.add(dao);
    }

    public static void sync() {
        for (BaseDao<? extends BeanObject> dao : DAO_LIST) {
            dao.sync();
        }
    }

    private static UserData obtainUserData(String username) {
        UserData userData = USER_DATA_LIST.get(username);
        if (userData == null) {
            synchronized (DaoManager.class) {
                userData = USER_DATA_LIST.get(username);
                if (userData == null) {
                    userData = new UserData();
                    USER_DATA_LIST.put(username, userData);
                }
            }
        }
        return userData;
    }

    public static void login(String username) {
        UserData userData = obtainUserData(username);
        if (userData.daoImpl.isEmpty()) {
            for (BaseDao<? extends BeanObject> dao : DAO_LIST) {
                userData.daoImpl.put(dao, dao.createDaoImpl());
            }
        }
    }

    public static void logout(String username) {
        USER_DATA_LIST.remove(username);
    }

    public static boolean switchUser(String username) {
        UserData userData = USER_DATA_LIST.get(username);
        if (userData == null) {
            return false;
        }

        for (BaseDao<? extends BeanObject> dao : DAO_LIST) {
            DaoImpl impl = userData.daoImpl.get(dao);
            dao.switchDaoImpl(impl);
        }
        if (!userData.bInit) {
            synchronized (DaoManager.class) {
                if (!userData.bInit) {
                    PropertyService propertyService = AppUtils.getBean(PropertyService.class);
                    propertyService.getBaseDao().loadData();
                    for (Map.Entry<BaseDao<? extends BeanObject>, DaoImpl> entry : userData.daoImpl.entrySet()) {
                        entry.getKey().loadData();
                    }
                }
                userData.bInit = true;
            }
        }
        return true;
    }

    private static class UserData {
        boolean bInit;
        Map<BaseDao<? extends BeanObject>, DaoImpl> daoImpl = new HashMap<>();
    }
}
