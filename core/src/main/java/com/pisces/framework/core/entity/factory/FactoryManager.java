package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.config.BaseProperties;
import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.io.FileUtils;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.util.*;

/**
 * 工厂经理
 *
 * @author jason
 * @date 2022/12/07
 */
public final class FactoryManager {
    private static final Map<Class<? extends BaseObject>, EntityFactory> FACTORIES = new HashMap<>();
    private static final Map<String, EntityFactory> FACTORY_NAMES = new HashMap<>();
    private static final Map<String, BaseProperties> MODEL_PROPERTIES = new HashMap<>();

    static {
        EntityFactory factory = new EntityFactory(BaseObject.class);
        FactoryManager.FACTORIES.put(BaseObject.class, factory);
        FactoryManager.FACTORY_NAMES.put(BaseObject.class.getSimpleName(), factory);
    }

    private FactoryManager() {
    }

    public static void registerEntityClass(Class<? extends BaseObject> clazz) {
        if (FACTORIES.containsKey(clazz)) {
            return;
        }
        EntityFactory factory = new EntityFactory(clazz);
        FactoryManager.FACTORIES.put(clazz, factory);
        FactoryManager.FACTORY_NAMES.put(clazz.getSimpleName(), factory);
        registerEntityClass((Class<? extends BaseObject>) clazz.getSuperclass());
    }

    public static void init() {
        initModelProperties();
        for (Map.Entry<Class<? extends BaseObject>, EntityFactory> entry : FACTORIES.entrySet()) {
            Class<? extends BaseObject> clazz = entry.getKey();
            Class<? extends BaseObject> superClazz = (Class<? extends BaseObject>) clazz.getSuperclass();
            EntityFactory factory = FactoryManager.FACTORIES.get(clazz);
            EntityFactory superFactory = FactoryManager.FACTORIES.get(superClazz);
            if (factory != null && superFactory != null) {
                factory.superFactory = superFactory;
                superFactory.childFactories.add(factory);
            }
        }

        initEntityModel();
    }

    private static void initModelProperties() {
        MODEL_PROPERTIES.clear();
        Map<String, BaseProperties> properties = AppUtils.getBeansOfType(BaseProperties.class);
        for (Map.Entry<String, BaseProperties> entry : properties.entrySet()) {
            BaseProperties property = entry.getValue();
            if (StringUtils.isEmpty(property.getIdentify())) {
                throw new UnsupportedOperationException(entry.getKey() + "`s Model Identify can not be empty!");
            }
            if (MODEL_PROPERTIES.containsKey(property.getIdentify())) {
                throw new UnsupportedOperationException(entry.getKey() + "`s Model Identify " + property.getIdentify() + " has existed!");
            }
            MODEL_PROPERTIES.put(property.getIdentify(), property);
        }
    }

    private static void initEntityModel() {
        for (Map.Entry<String, BaseProperties> entry : MODEL_PROPERTIES.entrySet()) {
            List<Class<?>> beanClasses = FileUtils.loadClass(entry.getValue().getBeanPackage());
            for (Class<?> beanClass : beanClasses) {
                if (!BaseObject.class.isAssignableFrom(beanClass)) {
                    continue;
                }
                EntityFactory factory = FACTORIES.get(beanClass);
                if (factory == null) {
                    continue;
                }
                factory.identify = entry.getValue().getIdentify();
            }
        }
    }

    public static String getModelIdentify(String entityName) {
        return fetchFactory(entityName).identify;
    }

    public static Set<Class<? extends BaseObject>> getEntityClasses() {
        return FACTORIES.keySet();
    }

    public static Class<? extends BaseObject> fetchEntityClass(String entityName) {
        return fetchFactory(entityName).getEntityClass();
    }

    public static List<EntityFactory> getFactories() {
        return new ArrayList<>(FactoryManager.FACTORIES.values());
    }

    public static EntityFactory fetchFactory(Class<? extends BaseObject> entityClass) {
        EntityFactory factory = FactoryManager.FACTORIES.get(entityClass);
        if (factory == null) {
            throw new UnsupportedOperationException(entityClass.getName() + " has not registered!");
        }
        return factory;
    }

    public static boolean hasFactory(Class<? extends BaseObject> entityClass) {
        return FactoryManager.FACTORIES.containsKey(entityClass);
    }

    public static EntityFactory fetchFactory(String entityName) {
        EntityFactory entityFactory = FactoryManager.FACTORY_NAMES.get(entityName);
        if (entityFactory == null) {
            throw new UnsupportedOperationException(entityName + " has not registered!");
        }
        return entityFactory;
    }


}
