package com.pisces.framework.core.utils.lang;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.pisces.framework.core.converter.DateTimeDeserializer;
import com.pisces.framework.core.converter.DateTimeSerializer;
import com.pisces.framework.core.converter.SqlDateDeserializer;
import com.pisces.framework.core.converter.SqlDateSerializer;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.entity.factory.AbstractFactory;
import com.pisces.framework.core.entity.factory.AbstractFactoryCreator;
import com.pisces.framework.core.entity.factory.FactoryManager;
import com.pisces.framework.core.entity.serializer.EntityDeserializerModifier;
import com.pisces.framework.core.entity.serializer.EntityMapper;
import com.pisces.framework.core.exception.ConfigurationException;
import com.pisces.framework.core.service.BeanService;
import com.pisces.framework.core.service.ServiceManager;
import com.pisces.framework.core.validator.constraints.PrimaryKey;
import com.pisces.framework.type.Duration;
import com.pisces.framework.type.EDIT_TYPE;
import com.pisces.framework.type.PROPERTY_TYPE;
import com.pisces.framework.type.annotation.PropertyMeta;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

/**
 * 实体跑龙套
 *
 * @author jason
 * @date 2022/12/07
 */
public final class ObjectUtils {
    private ObjectUtils() {
    }

    public static void init() {
        try {
            FactoryManager.init();
            checkProperty();
            checkBeanObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<? extends BeanObject>> getBeanClasses() {
        return FactoryManager.getBeanClasses();
    }

    public static Class<? extends BeanObject> fetchBeanClass(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name must not be null or blank.");
        }
        return FactoryManager.fetchBeanClass(name);
    }

    public static Class<? extends BeanObject> getSuperClass(Class<? extends BeanObject> beanClass) {
        AbstractFactory superFactory = FactoryManager.fetchFactory(beanClass).getSuperFactory();
        return superFactory != null ? superFactory.getBeanClass() : null;
    }

    public static List<Class<? extends BeanObject>> getChildClasses(Class<? extends BeanObject> beanClass) {
        List<Class<? extends BeanObject>> childClasses = new ArrayList<>();
        getChildClassesImpl(childClasses, beanClass);
        return childClasses;
    }

    private static void getChildClassesImpl(List<Class<? extends BeanObject>> result, Class<? extends BeanObject> beanClass) {
        List<AbstractFactory> childFactories = FactoryManager.fetchFactory(beanClass).getChildFactories();
        for (AbstractFactory childFactory : childFactories) {
            if (!Modifier.isAbstract(childFactory.getBeanClass().getModifiers())) {
                result.add(childFactory.getBeanClass());
            }
            getChildClassesImpl(result, childFactory.getBeanClass());
        }
    }

    public static <T extends BeanObject> T getInherit(Class<T> beanClass, long id) {
        BeanService<T> service = ServiceManager.getService(beanClass);
        T bean = service != null ? service.getById(id) : null;
        if (bean == null) {
            List<Class<? extends BeanObject>> childClasses = getChildClasses(beanClass);
            for (Class<? extends BeanObject> childClass : childClasses) {
                bean = (T) getInherit(childClass, id);
                if (bean != null) {
                    return bean;
                }
            }
        }

        return bean;
    }

    public static <T extends BeanObject> List<T> getInherit(Class<T> beanClass, List<Long> ids) {
        if (getChildClasses(beanClass).isEmpty()) {
            return ServiceManager.getService(beanClass).listByIds(ids);
        }

        List<T> result = new ArrayList<>();
        for (Long id : ids) {
            T entity = getInherit(beanClass, id);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    public static <T extends BeanObject> List<T> listInherit(Class<T> beanClass) {
        List<T> result = new ArrayList<>();
        listInheritImpl(result, beanClass, null);
        return result;
    }

    public static <T extends BeanObject> List<T> listInherit(Class<T> beanClass, Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        listInheritImpl(result, beanClass, filter);
        return result;
    }

    private static <T extends BeanObject> void listInheritImpl(List<T> result, Class<? extends T> beanClass, Predicate<T> filter) {
        BeanService<? extends T> service = ServiceManager.getService(beanClass);
        if (service != null) {
            if (filter != null) {
                List<? extends T> beans = service.list();
                for (T entity : beans) {
                    if (filter.test(entity)) {
                        result.add(entity);
                    }
                }
            } else {
                result.addAll(service.list());
            }
        }

        List<Class<? extends BeanObject>> childClasses = getChildClasses(beanClass);
        for (Class<? extends BeanObject> childClass : childClasses) {
            listInheritImpl(result, (Class<? extends T>) childClass, filter);
        }
    }

    public static Map<String, Property> getDefaultProperties(Class<? extends BeanObject> beanClass) {
        Map<String, Property> result = new HashMap<>(16);
        getDefaultPropertiesImpl(result, beanClass, beanClass, 0);
        return result;
    }

    private static int getDefaultPropertiesImpl(Map<String, Property> result, Class<? extends BeanObject> beanClass, Class<? extends BeanObject> belongClass, int startOrder) {
        if (beanClass == null) {
            return startOrder;
        }
        if (beanClass == BeanObject.class && startOrder == 0) {
            return startOrder;
        }

        int orderNumber = getDefaultPropertiesImpl(result, getSuperClass(beanClass), belongClass, startOrder);

        Map<String, Property> codeMapProperties = new HashMap<>(16);
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                Property property = createProperty(beanClass, belongClass, field);
                if (property != null) {
                    property.setOrderNumber(++orderNumber);
                    result.put(property.getPropertyCode(), property);
                    codeMapProperties.put(property.getPropertyCode(), property);
                }
            } catch (Exception ignored) {
            }
        }
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            Property property = createReadOnlyProperty(beanClass, belongClass, method);
            if (property != null) {
                property.setOrderNumber(++orderNumber);
                result.put(property.getPropertyCode(), property);
                codeMapProperties.put(property.getPropertyCode(), property);
            }
        }

        if (beanClass == belongClass) {
            getDefaultPropertiesImpl(result, BeanObject.class, belongClass, orderNumber);
        }

        PrimaryKey primaryKey = beanClass.getAnnotation(PrimaryKey.class);
        if (primaryKey != null) {
            String[] primaryFields = primaryKey.fields();
            for (String primaryField : primaryFields) {
                Property property = codeMapProperties.get(primaryField);
                if (property == null) {
                    throw new ConfigurationException(beanClass.getName() + " config primary key has error field name: " + primaryField);
                }

                property.setPrimaryKey(true);
                property.setModify(false);
                property.setPopDisplay(true);
            }
        }
        return orderNumber;
    }

    private static Property createProperty(Class<? extends BeanObject> beanClass, Class<? extends BeanObject> belongClass, Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return null;
        }
        PropertyMeta meta = field.getAnnotation(PropertyMeta.class);
        if (meta != null && !meta.property()) {
            return null;
        }
        Class<?> propertyClass = field.getType();
        PROPERTY_TYPE propertyType = ClassUtils.getPropertyType(propertyClass);
        if (propertyType == PROPERTY_TYPE.LIST) {
            return null;
        }
        Property property = new Property();
        property.init();
        property.setInherent(true);
        property.setBelongName(belongClass.getSimpleName());
        property.setPropertyCode(field.getName());

        property.setType(propertyType);
        property.setTypeName(propertyClass.getSimpleName());
        property.setTypeFullName(propertyClass.getName());

        if (meta != null) {
            property.setSystem(meta.system());
            property.setModify(meta.modify());
            property.setLarge(meta.large());
            property.setPopDisplay(meta.popDisplay());
            if (meta.type() != PROPERTY_TYPE.NONE) {
                property.setType(meta.type());
            }
            if (meta.editType() != EDIT_TYPE.NONE) {
                property.setEditType(meta.editType().name());
            }
        }
        if (beanClass == BeanObject.class) {
            property.setModify(false);
        }
        property.setField(field);

        return property;
    }

    private static Property createReadOnlyProperty(Class<? extends BeanObject> beanClass, Class<? extends BeanObject> belongClass, Method method) {
        PropertyMeta meta = method.getAnnotation(PropertyMeta.class);
        if (meta == null || !meta.property()) {
            return null;
        }
        Property property = new Property();
        property.init();
        property.setBelongName(belongClass.getSimpleName());
        String code = method.getName().substring(3);
        code = code.substring(0, 1).toLowerCase() + code.substring(1);
        property.setPropertyCode(code);

        Class<?> propertyClass = method.getReturnType();
        property.setType(ClassUtils.getPropertyType(propertyClass));
        property.setTypeName(propertyClass.getSimpleName());
        property.setTypeFullName(propertyClass.getName());

        property.setSystem(meta.system());
        property.setModify(false);
        property.setLarge(meta.large());
        property.setPopDisplay(meta.popDisplay());
        if (meta.type() != PROPERTY_TYPE.NONE) {
            property.setType(meta.type());
        }
        if (meta.editType() != EDIT_TYPE.NONE) {
            property.setEditType(meta.editType().name());
        }
        if (beanClass == BeanObject.class) {
            property.setModify(false);
        }
        property.setField(null);
        property.setGetMethod(method);

        return property;
    }

    public static void checkProperty() throws Exception {
        for (Class<? extends BeanObject> beanClass : getBeanClasses()) {
            Field[] fields = beanClass.getDeclaredFields();
            Map<String, Field> fieldCache = new HashMap<>(16);
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                if (Modifier.isStatic(field.getModifiers())) {
                    for (AbstractFactoryCreator factoryCreator : FactoryManager.getFactoryCreators()) {
                        if (factoryCreator.checkProperty(beanClass, field)) {
                            fieldCache.put(field.getName(), field);
                            break;
                        }
                    }
                    continue;
                }

                PropertyMeta meta = field.getAnnotation(PropertyMeta.class);
                if (meta != null && !meta.property()) {
                    continue;
                }

                Class<?> fieldClass;
                PROPERTY_TYPE type = ClassUtils.getPropertyType(field.getType());
                fieldClass = field.getType();
                Method getMethod = null;
                Method setMethod = null;
                try {
                    getMethod = beanClass.getMethod("get" + org.springframework.util.StringUtils.capitalize(field.getName()));
                } catch (NoSuchMethodException | SecurityException ignored) {
                }
                try {
                    setMethod = beanClass.getMethod("set" + org.springframework.util.StringUtils.capitalize(field.getName()), fieldClass);
                } catch (NoSuchMethodException | SecurityException ignored) {
                }

                if (getMethod == null) {
                    throw new NoSuchMethodException(beanClass.getName() + "`s Field " + field.getName() + " has not get method!");
                }
                if (setMethod == null && type != PROPERTY_TYPE.LIST) {
                    throw new NoSuchMethodException(beanClass.getName() + "`s Field " + field.getName() + " has not set method!");
                }

                fieldCache.put(field.getName(), field);
            }

            PrimaryKey primaryKey = beanClass.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                String[] primaryFields = primaryKey.fields();
                for (String primaryField : primaryFields) {
                    Field field = fieldCache.get(primaryField);
                    if (field == null) {
                        throw new ConfigurationException(beanClass.getName() + " config primary key has error field name: " + primaryField);
                    }
                }
            }
        }
    }

    public static void checkBeanObject() throws Exception {
        for (Class<? extends BeanObject> beanClass : getBeanClasses()) {
            if (Modifier.isAbstract(beanClass.getModifiers())) {
                continue;
            }
            if ("Property".equals(beanClass.getSimpleName()) || "BaseObject".equals(beanClass.getSimpleName())) {
                continue;
            }
            BeanObject bean = BeanUtils.instantiateClass(beanClass);
            bean.init();
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                boolean isAccessible = field.canAccess(bean);
                field.setAccessible(true);
                if (field.get(bean) == null) {
                    throw new NullPointerException(beanClass.getName() + "`s field " + field.getName() + " has not default value.");
                }
                field.setAccessible(isAccessible);
            }
        }
    }

    public static Class<?> getTypeClass(PROPERTY_TYPE type) {
        if (type == null) {
            return null;
        }

        Class<?> typeClass = null;
        switch (type) {
            case BOOLEAN -> typeClass = Boolean.class;
            case CHAR -> typeClass = Character.class;
            case SHORT -> typeClass = Short.class;
            case INTEGER -> typeClass = Integer.class;
            case LONG -> typeClass = Long.class;
            case DOUBLE -> typeClass = Double.class;
            case DATE, TIME, DATE_TIME -> typeClass = Date.class;
            case DURATION -> typeClass = Duration.class;
            case STRING -> typeClass = String.class;
            default -> {
            }
        }
        return typeClass;
    }

    public static EDIT_TYPE getEditType(PROPERTY_TYPE type) {
        EDIT_TYPE editType = EDIT_TYPE.TEXT;
        switch (type) {
            case BOOLEAN -> editType = EDIT_TYPE.BOOLEAN;
            case CHAR -> editType = EDIT_TYPE.CHAR;
            case SHORT, INTEGER, LONG -> editType = EDIT_TYPE.NUMBER;
            case DOUBLE -> editType = EDIT_TYPE.DOUBLE;
            case DATE -> editType = EDIT_TYPE.DATE;
            case TIME -> editType = EDIT_TYPE.TIME;
            case DATE_TIME -> editType = EDIT_TYPE.DATE_TIME;
            case DURATION -> editType = EDIT_TYPE.DURATION;
            case ENUM -> editType = EDIT_TYPE.ENUM;
            case MULTI_ENUM -> editType = EDIT_TYPE.MULTI_ENUM;
            case BEAN -> editType = EDIT_TYPE.ENTITY;
            case LIST -> editType = EDIT_TYPE.MULTI_ENTITY;
            default -> {
            }
        }
        return editType;
    }

    public static EntityMapper createBeanMapper() {
        EntityMapper mapper = new EntityMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Date.class, new DateTimeSerializer());
        module.addDeserializer(Date.class, new DateTimeDeserializer());
        module.addSerializer(java.sql.Date.class, new SqlDateSerializer());
        module.addDeserializer(java.sql.Date.class, new SqlDateDeserializer());
        module.setDeserializerModifier(new EntityDeserializerModifier());
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.bind();
        mapper.registerModule(module);
        mapper.setDefaultSetterInfo(JsonSetter.Value.construct(Nulls.SKIP, Nulls.SKIP));
        return mapper;
    }

    private static final ThreadLocal<EntityMapper> DEFAULT_MAPPER = new ThreadLocal<>();

    public static EntityMapper defaultBeanMapper() {
        if (DEFAULT_MAPPER.get() == null) {
            synchronized (ObjectUtils.class) {
                if (DEFAULT_MAPPER.get() == null) {
                    DEFAULT_MAPPER.set(createBeanMapper());
                }
            }
        }
        return DEFAULT_MAPPER.get();
    }

    public static <T extends BeanObject> boolean isAll(Collection<T> beans, Predicate<T> fun) {
        for (T entity : beans) {
            if (!fun.test(entity)) {
                return false;
            }
        }
        return !beans.isEmpty();
    }

    public static <T extends BeanObject> boolean isAny(Collection<T> beans, Predicate<T> fun) {
        for (T entity : beans) {
            if (fun.test(entity)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends BeanObject> boolean isNone(Collection<T> beans, Predicate<T> fun) {
        return !isAny(beans, fun);
    }

    public static <T extends BeanObject> void sort(List<T> beans, Comparator<T> c) {
        beans.sort((o1, o2) -> {
            int value = c.compare(o1, o2);
            return value != 0 ? value : o1.compareTo(o2);
        });
    }

    /**
     * map转对象
     *
     * @param map         地图
     * @param objectClass bean类
     * @return {@link T}
     * @throws Exception 异常
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> objectClass) throws Exception {
        T object = BeanUtils.instantiateClass(objectClass);
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                field.set(object, map.get(field.getName()));
            }
        }
        return object;
    }
}
