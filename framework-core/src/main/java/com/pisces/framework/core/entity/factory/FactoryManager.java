package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.config.BaseProperties;
import com.pisces.framework.core.entity.BeanObject;
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
    private static final List<FactoryCreator> FACTORY_CREATERS = new ArrayList<>();
    private static final Map<Class<? extends BeanObject>, BeanFactory> FACTORIES = new HashMap<>();
    private static final Map<String, BeanFactory> FACTORY_NAMES = new HashMap<>();
    private static final Map<String, BaseProperties> MODEL_PROPERTIES = new HashMap<>();

    static {
        BeanFactory factory = new BeanFactory(BeanObject.class);
        FactoryManager.FACTORIES.put(BeanObject.class, factory);
        FactoryManager.FACTORY_NAMES.put(BeanObject.class.getSimpleName(), factory);
    }

    private FactoryManager() {
    }

    public static void registerCreater(FactoryCreator creator) {
        FACTORY_CREATERS.add(creator);
    }

    private static BeanFactory createFactory(Class<? extends BeanObject> beanClass) {
        for (FactoryCreator creator : FACTORY_CREATERS) {
            BeanFactory factory = creator.create(beanClass);
            if (factory != null) {
                return factory;
            }
        }
        return new BeanFactory(beanClass);
    }

    public static void registerBeanClass(Class<? extends BeanObject> beanClass) {
        if (FACTORIES.containsKey(beanClass)) {
            return;
        }
        BeanFactory factory = createFactory(beanClass);
        FactoryManager.FACTORIES.put(beanClass, factory);
        FactoryManager.FACTORY_NAMES.put(beanClass.getSimpleName(), factory);
        registerBeanClass((Class<? extends BeanObject>) beanClass.getSuperclass());
    }

    public static void init() {
        initModelProperties();
        for (Map.Entry<Class<? extends BeanObject>, BeanFactory> entry : FACTORIES.entrySet()) {
            Class<? extends BeanObject> beanClass = entry.getKey();
            Class<? extends BeanObject> superClass = (Class<? extends BeanObject>) beanClass.getSuperclass();
            BeanFactory factory = FactoryManager.FACTORIES.get(beanClass);
            BeanFactory superFactory = FactoryManager.FACTORIES.get(superClass);
            if (factory != null && superFactory != null) {
                factory.superFactory = superFactory;
                superFactory.childFactories.add(factory);
            }
        }

        initBeanObjectModel();
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

    private static void initBeanObjectModel() {
        for (Map.Entry<String, BaseProperties> entry : MODEL_PROPERTIES.entrySet()) {
            List<Class<?>> beanClasses = FileUtils.loadClass(entry.getValue().getBeanPackage());
            for (Class<?> beanClass : beanClasses) {
                if (!BeanObject.class.isAssignableFrom(beanClass)) {
                    continue;
                }
                BeanFactory factory = FACTORIES.get(beanClass);
                if (factory == null) {
                    continue;
                }
                factory.identify = entry.getValue().getIdentify();
            }
        }
    }

    public static String getModelIdentify(String objectName) {
        return fetchFactory(objectName).identify;
    }

    public static Set<Class<? extends BeanObject>> getBeanClasses() {
        return FACTORIES.keySet();
    }

    public static Class<? extends BeanObject> fetchBeanClass(String objectName) {
        return fetchFactory(objectName).getBeanClass();
    }

    public static List<BeanFactory> getFactories() {
        return new ArrayList<>(FactoryManager.FACTORIES.values());
    }

    public static <T extends BeanFactory> T fetchFactory(Class<? extends BeanObject> beanClass) {
        BeanFactory factory = FactoryManager.FACTORIES.get(beanClass);
        if (factory == null) {
            throw new UnsupportedOperationException(beanClass.getName() + " has not registered!");
        }
        return (T) factory;
    }

    public static boolean hasFactory(Class<? extends BeanObject> beanClass) {
        return FactoryManager.FACTORIES.containsKey(beanClass);
    }

    public static BeanFactory fetchFactory(String objectName) {
        BeanFactory factory = FactoryManager.FACTORY_NAMES.get(objectName);
        if (factory == null) {
            throw new UnsupportedOperationException(objectName + " has not registered!");
        }
        return factory;
    }
}
