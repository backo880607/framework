package com.pisces.framework.core.utils.lang;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.pisces.framework.core.annotation.PropertyMeta;
import com.pisces.framework.core.converter.DateTimeDeserializer;
import com.pisces.framework.core.converter.DateTimeSerializer;
import com.pisces.framework.core.converter.SqlDateDeserializer;
import com.pisces.framework.core.converter.SqlDateSerializer;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Duration;
import com.pisces.framework.core.entity.MultiEnum;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.entity.factory.AbstractFactory;
import com.pisces.framework.core.entity.factory.FactoryManager;
import com.pisces.framework.core.entity.serializer.EntityDeserializerModifier;
import com.pisces.framework.core.entity.serializer.EntityMapper;
import com.pisces.framework.core.enums.EDIT_TYPE;
import com.pisces.framework.core.enums.PROPERTY_TYPE;
import com.pisces.framework.core.exception.ConfigurationException;
import com.pisces.framework.core.service.BeanService;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.service.ServiceManager;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.validator.constraints.PrimaryKey;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
            checkEntity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<? extends BeanObject>> getBeanClasses() {
        return FactoryManager.getBeanClasses();
    }

    public static Class<? extends BeanObject> fetchBeanClass(String name) {
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
        Property property = new Property();
        property.init();
        property.setBelongName(belongClass.getSimpleName());
        property.setPropertyCode(field.getName());

        Class<?> propertyClass = field.getType();
        property.setType(getPropertyType(propertyClass));
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
        property.setType(getPropertyType(propertyClass));
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
                    continue;
                }

                PropertyMeta meta = field.getAnnotation(PropertyMeta.class);
                if (meta != null && !meta.property()) {
                    continue;
                }

                PROPERTY_TYPE type;
                Class<?> fieldClass;
                type = getPropertyType(field.getType());
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

//            PrimaryKey primaryKey = beanClass.getAnnotation(PrimaryKey.class);
//            if (primaryKey != null) {
//                String[] primaryFields = primaryKey.fields();
//                for (String primaryField : primaryFields) {
//                    Field field = fieldCache.get(primaryField);
//                    if (field == null) {
//                        throw new ConfigurationException(beanClass.getName() + " config primary key has error field name: " + primaryField);
//                    }
//                }
//            }
        }
    }

    public static void checkEntity() throws Exception {
        for (Class<? extends BeanObject> beanClass : getBeanClasses()) {
            if (Modifier.isAbstract(beanClass.getModifiers())) {
                continue;
            }
            if ("Property".equals(beanClass.getSimpleName()) || "BaseObject".equals(beanClass.getSimpleName())) {
                continue;
            }
            BeanObject entity = BeanUtils.instantiateClass(beanClass);
            entity.init();
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                boolean isAccessible = field.canAccess(entity);
                field.setAccessible(true);
                if (field.get(entity) == null) {
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

    public static PROPERTY_TYPE getPropertyType(Class<?> clazz) {
        PROPERTY_TYPE type;
        if (clazz == Boolean.class || clazz == boolean.class) {
            type = PROPERTY_TYPE.BOOLEAN;
        } else if (clazz == Short.class || clazz == short.class) {
            type = PROPERTY_TYPE.SHORT;
        } else if (clazz == Integer.class || clazz == int.class) {
            type = PROPERTY_TYPE.INTEGER;
        } else if (clazz == Long.class || clazz == long.class) {
            type = PROPERTY_TYPE.LONG;
        } else if (clazz == Double.class || clazz == double.class) {
            type = PROPERTY_TYPE.DOUBLE;
        } else if (clazz == String.class) {
            type = PROPERTY_TYPE.STRING;
        } else if (clazz == Date.class) {
            type = PROPERTY_TYPE.DATE_TIME;
        } else if (clazz == Duration.class) {
            type = PROPERTY_TYPE.DURATION;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.ENUM;
        } else if (MultiEnum.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.MULTI_ENUM;
        } else if (BeanObject.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.ENTITY;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            type = PROPERTY_TYPE.LIST;
        } else {
            throw new UnsupportedOperationException("not support type: " + clazz.getName());
        }
        return type;
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
            case ENTITY -> editType = EDIT_TYPE.ENTITY;
            case LIST -> editType = EDIT_TYPE.MULTI_ENTITY;
            default -> {
            }
        }
        return editType;
    }

    public static Object getValue(BeanObject entity, Property property) {
        if (entity == null || property == null || property.getGetMethod() == null) {
            return null;
        }

        Object value = null;
        try {
            value = property.getGetMethod().invoke(entity, property.getPropertyCode());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
        }

        return value;
    }

    public static void setValue(BeanObject entity, Property property, Object value) {
        if (entity == null || property == null || property.getSetMethod() == null) {
            return;
        } else if (value == null && property.getType() != PROPERTY_TYPE.ENTITY) {
            return;
        }

        try {
            property.getSetMethod().invoke(entity, property.getPropertyCode(), value);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    public static EntityMapper createEntityMapper() {
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

    private static final ThreadLocal<EntityMapper> defaultMapper = new ThreadLocal<>();

    public static EntityMapper defaultObjectMapper() {
        if (defaultMapper.get() == null) {
            synchronized (ObjectUtils.class) {
                if (defaultMapper.get() == null) {
                    defaultMapper.set(createEntityMapper());
                }
            }
        }
        return defaultMapper.get();
    }

    public static <T extends BeanObject> void copyIgnoreNull(T src, T target) {
        if (src.getClass() != target.getClass()) {
            return;
        }

        List<Property> properties = AppUtils.getBean(PropertyService.class).get(src.getClass());
        for (Property property : properties) {
            Object srcValue = getValue(src, property);
            if (property.getType() == PROPERTY_TYPE.ENTITY) {
                if (srcValue != null) {
                    final long relationId = ((BeanObject) srcValue).getId();
                    if (relationId < 0) {
                        continue;
                    }
                    srcValue = ServiceManager.fetchService((Class<? extends BeanObject>) property.getTypeClass()).getById(relationId);
                }
            } else if (srcValue == null) {
                continue;
            }

            setValue(target, property, srcValue);
        }
    }

    public static void cloneEntity(BeanObject origin, BeanObject target) {
        List<Property> properties = AppUtils.getBean(PropertyService.class).get(origin.getClass());
        for (Property property : properties) {
            if ("id".equals(property.getPropertyCode())) {
                continue;
            }
            if (property.getType() == PROPERTY_TYPE.LIST) {
                continue;
            }
            Object entityValue = ObjectUtils.getValue(origin, property);
            ObjectUtils.setValue(target, property, entityValue);
        }
    }

    public static String getPrimaryValue(BeanObject entity) {
        if (entity == null) {
            return "";
        }

        List<Property> properties = AppUtils.getBean(PropertyService.class).getPrimaries(entity.getClass());
        return StringUtils.join(properties, ",", (Property property) -> {
            Object value = getValue(entity, property);
            if (value == null) {
                return "";
            }
            try {
                return value.getClass() == String.class ? value.toString() : defaultObjectMapper().writeValueAsString(value);
            } catch (JsonProcessingException ignored) {
            }
            return "";
        });
    }

    public static <T extends BeanObject> boolean isAll(Collection<T> entities, Predicate<T> fun) {
        for (T entity : entities) {
            if (!fun.test(entity)) {
                return false;
            }
        }
        return !entities.isEmpty();
    }

    public static <T extends BeanObject> boolean isAny(Collection<T> entities, Predicate<T> fun) {
        for (T entity : entities) {
            if (fun.test(entity)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends BeanObject> boolean isNone(Collection<T> entities, Predicate<T> fun) {
        return !isAny(entities, fun);
    }

    public static <T extends BeanObject> void sort(List<T> entities, Comparator<T> c) {
        entities.sort((o1, o2) -> {
            int value = c.compare(o1, o2);
            return value != 0 ? value : o1.compareTo(o2);
        });
    }
}
