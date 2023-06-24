package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.config.BaseProperties;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.service.ServiceManager;
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
    private static final List<AbstractFactoryCreator> FACTORY_CREATORS = new ArrayList<>();
    private static final Map<Class<? extends BeanObject>, AbstractFactory> FACTORIES = new HashMap<>();
    private static final Map<String, AbstractFactory> FACTORY_NAMES = new HashMap<>();
    private static final Map<String, BaseProperties> MODEL_PROPERTIES = new HashMap<>();

    static {
        AbstractFactory factory = new BeanFactory(BeanObject.class);
        FactoryManager.FACTORIES.put(BeanObject.class, factory);
        FactoryManager.FACTORY_NAMES.put(BeanObject.class.getSimpleName(), factory);
    }

    private FactoryManager() {
    }

    public static void registerCreator(AbstractFactoryCreator creator) {
        FACTORY_CREATORS.add(creator);
    }

    private static AbstractFactory createFactory(Class<? extends BeanObject> beanClass) {
        for (AbstractFactoryCreator creator : FACTORY_CREATORS) {
            AbstractFactory factory = creator.create(beanClass);
            if (factory != null) {
                return factory;
            }
        }
        return new BeanFactory(beanClass);
    }

    public static void initBeanFactory(Class<? extends BeanObject> beanClass) {
        if (FACTORIES.containsKey(beanClass)) {
            return;
        }
        AbstractFactory factory = createFactory(beanClass);
        FactoryManager.FACTORIES.put(beanClass, factory);
        FactoryManager.FACTORY_NAMES.put(beanClass.getSimpleName(), factory);
        initBeanFactory((Class<? extends BeanObject>) beanClass.getSuperclass());
    }

    private static void initBeanFactory() {
        Set<Class<? extends BeanObject>> beanClasses = ServiceManager.getBeanClasses();
        for (Class<? extends BeanObject> beanClass : beanClasses) {
            initBeanFactory(beanClass);
        }
    }

    public static void init() {
        initBeanFactory();
        initModelProperties();
        for (Map.Entry<Class<? extends BeanObject>, AbstractFactory> entry : FACTORIES.entrySet()) {
            Class<? extends BeanObject> beanClass = entry.getKey();
            AbstractFactory factory = entry.getValue();
            Class<? extends BeanObject> superClass = (Class<? extends BeanObject>) beanClass.getSuperclass();
            AbstractFactory superFactory = FactoryManager.FACTORIES.get(superClass);
            if (factory != null && superFactory != null) {
                factory.superFactory = superFactory;
                superFactory.childFactories.add(factory);
            }
        }

        initBeanObjectModel();
        for (AbstractFactoryCreator creator : FACTORY_CREATORS) {
            creator.init();
        }
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
                AbstractFactory factory = FACTORIES.get(beanClass);
                if (factory == null) {
                    continue;
                }
                factory.identify = entry.getValue().getIdentify();
            }
        }
    }

    public static String getModelIdentify(String beanName) {
        return fetchFactory(beanName).identify;
    }

    public static Set<Class<? extends BeanObject>> getBeanClasses() {
        return FACTORIES.keySet();
    }

    public static Class<? extends BeanObject> fetchBeanClass(String beanName) {
        return fetchFactory(beanName).getBeanClass();
    }

    public static AbstractFactory fetchFactory(Class<? extends BeanObject> beanClass) {
        AbstractFactory factory = FactoryManager.FACTORIES.get(beanClass);
        if (factory == null) {
            throw new UnsupportedOperationException(beanClass.getName() + " has not registered!");
        }
        return factory;
    }

    public static boolean hasFactory(Class<? extends BeanObject> beanClass) {
        return FactoryManager.FACTORIES.containsKey(beanClass);
    }

    public static AbstractFactory fetchFactory(String beanName) {
        AbstractFactory factory = FactoryManager.FACTORY_NAMES.get(beanName);
        if (factory == null) {
            throw new UnsupportedOperationException(beanName + " has not registered!");
        }
        return factory;
    }
}
