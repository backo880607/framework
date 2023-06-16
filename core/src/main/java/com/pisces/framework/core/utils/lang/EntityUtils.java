package com.pisces.framework.core.utils.lang;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.pisces.framework.core.annotation.PropertyMeta;
import com.pisces.framework.core.converter.*;
import com.pisces.framework.core.entity.*;
import com.pisces.framework.core.entity.factory.EntityFactory;
import com.pisces.framework.core.entity.factory.FactoryManager;
import com.pisces.framework.core.entity.serializer.EntityDeserializerModifier;
import com.pisces.framework.core.entity.serializer.EntityMapper;
import com.pisces.framework.core.enums.EDIT_TYPE;
import com.pisces.framework.core.enums.PROPERTY_TYPE;
import com.pisces.framework.core.exception.ConfigurationException;
import com.pisces.framework.core.service.BaseService;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.service.ServiceManager;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.validator.constraints.PrimaryKey;

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
public final class EntityUtils {
    private EntityUtils() {
    }

    public static void init() {
        try {
            checkProperty();
            checkEntity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<? extends BaseObject>> getEntityClasses() {
        return FactoryManager.getEntityClasses();
    }

    public static Class<? extends BaseObject> fetchEntityClass(String name) {
        return FactoryManager.fetchEntityClass(name);
    }

    public static Class<? extends BaseObject> getSuperClass(Class<? extends BaseObject> entityClass) {
        EntityFactory superFactory = FactoryManager.fetchFactory(entityClass).getSuperFactory();
        return superFactory != null ? superFactory.getEntityClass() : null;
    }

    public static List<Class<? extends BaseObject>> getChildClasses(Class<? extends BaseObject> entityClass) {
        List<Class<? extends BaseObject>> childClasses = new ArrayList<>();
        getChildClassesImpl(childClasses, entityClass);
        return childClasses;
    }

    private static void getChildClassesImpl(List<Class<? extends BaseObject>> result, Class<? extends BaseObject> entityClass) {
        List<EntityFactory> childFactories = FactoryManager.fetchFactory(entityClass).getChildFactories();
        for (EntityFactory childFactory : childFactories) {
            if (!Modifier.isAbstract(childFactory.getEntityClass().getModifiers())) {
                result.add(childFactory.getEntityClass());
            }
            getChildClassesImpl(result, childFactory.getEntityClass());
        }
    }

    public static <T extends BaseObject> T getInherit(Class<T> entityClass, long id) {
        BaseService<T> service = ServiceManager.getService(entityClass);
        T entity = service != null ? service.getById(id) : null;
        if (entity == null) {
            List<Class<? extends BaseObject>> childClasses = getChildClasses(entityClass);
            for (Class<? extends BaseObject> childClass : childClasses) {
                entity = (T) getInherit(childClass, id);
                if (entity != null) {
                    return entity;
                }
            }
        }

        return entity;
    }

    public static <T extends BaseObject> List<T> getInherit(Class<T> entityClass, List<Long> ids) {
        if (getChildClasses(entityClass).isEmpty()) {
            return ServiceManager.getService(entityClass).listByIds(ids);
        }

        List<T> result = new ArrayList<>();
        for (Long id : ids) {
            T entity = getInherit(entityClass, id);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    public static <T extends BaseObject> List<T> listInherit(Class<T> entityClass) {
        List<T> result = new ArrayList<>();
        listInheritImpl(result, entityClass, null);
        return result;
    }

    public static <T extends BaseObject> List<T> listInherit(Class<T> entityClass, Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        listInheritImpl(result, entityClass, filter);
        return result;
    }

    private static <T extends BaseObject> void listInheritImpl(List<T> result, Class<? extends T> entityClass, Predicate<T> filter) {
        BaseService<? extends T> service = ServiceManager.getService(entityClass);
        if (service != null) {
            if (filter != null) {
                List<? extends T> entities = service.list();
                for (T entity : entities) {
                    if (filter.test(entity)) {
                        result.add(entity);
                    }
                }
            } else {
                result.addAll(service.list());
            }
        }

        List<Class<? extends BaseObject>> childClasses = getChildClasses(entityClass);
        for (Class<? extends BaseObject> childClass : childClasses) {
            listInheritImpl(result, (Class<? extends T>) childClass, filter);
        }
    }

    public static Map<String, Property> getDefaultProperties(Class<? extends BaseObject> entityClass) {
        Map<String, Property> result = new HashMap<>(16);
        getDefaultPropertiesImpl(result, entityClass, entityClass, 0);
        return result;
    }

    private static int getDefaultPropertiesImpl(Map<String, Property> result, Class<? extends BaseObject> entityClass, Class<? extends BaseObject> belongClass, int startOrder) {
        if (entityClass == null) {
            return startOrder;
        }
        if (entityClass == BaseObject.class && startOrder == 0) {
            return startOrder;
        }

        int orderNumber = getDefaultPropertiesImpl(result, getSuperClass(entityClass), belongClass, startOrder);

        Map<String, Property> codeMapProperties = new HashMap<>(16);
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                Property property = createProperty(entityClass, belongClass, field);
                if (property != null) {
                    property.setOrderNumber(++orderNumber);
                    result.put(property.getPropertyCode(), property);
                    codeMapProperties.put(property.getPropertyCode(), property);
                }
            } catch (Exception ignored) {
            }
        }
        Method[] methods = entityClass.getDeclaredMethods();
        for (Method method : methods) {
            Property property = createReadOnlyProperty(entityClass, belongClass, method);
            if (property != null) {
                property.setOrderNumber(++orderNumber);
                result.put(property.getPropertyCode(), property);
                codeMapProperties.put(property.getPropertyCode(), property);
            }
        }

        if (entityClass == belongClass) {
            getDefaultPropertiesImpl(result, BaseObject.class, belongClass, orderNumber);
        }

        PrimaryKey primaryKey = entityClass.getAnnotation(PrimaryKey.class);
        if (primaryKey != null) {
            String[] primaryFields = primaryKey.fields();
            for (String primaryField : primaryFields) {
                Property property = codeMapProperties.get(primaryField);
                if (property == null) {
                    throw new ConfigurationException(entityClass.getName() + " config primary key has error field name: " + primaryField);
                }

                property.setPrimaryKey(true);
                property.setModify(false);
                property.setPopDisplay(true);
            }
        }
        return orderNumber;
    }

    private static Property createProperty(Class<? extends BaseObject> entityClass, Class<? extends BaseObject> belongClass, Field field) {
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
        if (entityClass == BaseObject.class) {
            property.setModify(false);
        }
        property.setField(field);

        return property;
    }

    private static Property createReadOnlyProperty(Class<? extends BaseObject> entityClass, Class<? extends BaseObject> belongClass, Method method) {
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
        if (entityClass == BaseObject.class) {
            property.setModify(false);
        }
        property.setField(null);
        property.setGetMethod(method);

        return property;
    }

    public static void checkProperty() throws Exception {
        for (Class<? extends BaseObject> entityClass : getEntityClasses()) {
            Field[] fields = entityClass.getDeclaredFields();
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
                    getMethod = entityClass.getMethod("get" + org.springframework.util.StringUtils.capitalize(field.getName()));
                } catch (NoSuchMethodException | SecurityException ignored) {
                }
                try {
                    setMethod = entityClass.getMethod("set" + org.springframework.util.StringUtils.capitalize(field.getName()), fieldClass);
                } catch (NoSuchMethodException | SecurityException ignored) {
                }

                if (getMethod == null) {
                    throw new NoSuchMethodException(entityClass.getName() + "`s Field " + field.getName() + " has not get method!");
                }
                if (setMethod == null && type != PROPERTY_TYPE.LIST) {
                    throw new NoSuchMethodException(entityClass.getName() + "`s Field " + field.getName() + " has not set method!");
                }

                fieldCache.put(field.getName(), field);
            }

            PrimaryKey primaryKey = entityClass.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                String[] primaryFields = primaryKey.fields();
                for (String primaryField : primaryFields) {
                    Field field = fieldCache.get(primaryField);
                    if (field == null) {
                        throw new ConfigurationException(entityClass.getName() + " config primary key has error field name: " + primaryField);
                    }
                }
            }
        }
    }

    public static void checkEntity() throws Exception {
        for (Class<? extends BaseObject> entityClass : getEntityClasses()) {
            if (Modifier.isAbstract(entityClass.getModifiers())) {
                continue;
            }
            if ("Property".equals(entityClass.getSimpleName()) || "BaseObject".equals(entityClass.getSimpleName())) {
                continue;
            }
            BaseObject entity = entityClass.newInstance();
            entity.init();
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                if (field.get(entity) == null) {
                    throw new NullPointerException(entityClass.getName() + "`s field " + field.getName() + " has not default value.");
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
            case BOOLEAN:
                typeClass = Boolean.class;
                break;
            case CHAR:
                typeClass = Character.class;
                break;
            case SHORT:
                typeClass = Short.class;
                break;
            case INTEGER:
                typeClass = Integer.class;
                break;
            case LONG:
                typeClass = Long.class;
                break;
            case DOUBLE:
                typeClass = Double.class;
                break;
            case DATE:
            case TIME:
            case DATE_TIME:
                typeClass = Date.class;
                break;
            case DURATION:
                typeClass = Duration.class;
                break;
            case STRING:
                typeClass = String.class;
                break;
            default:
                break;
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
        } else if (BaseObject.class.isAssignableFrom(clazz)) {
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
            case BOOLEAN:
                editType = EDIT_TYPE.BOOLEAN;
                break;
            case CHAR:
                editType = EDIT_TYPE.CHAR;
                break;
            case SHORT:
            case INTEGER:
            case LONG:
                editType = EDIT_TYPE.NUMBER;
                break;
            case DOUBLE:
                editType = EDIT_TYPE.DOUBLE;
                break;
            case DATE:
                editType = EDIT_TYPE.DATE;
                break;
            case TIME:
                editType = EDIT_TYPE.TIME;
                break;
            case DATE_TIME:
                editType = EDIT_TYPE.DATE_TIME;
                break;
            case DURATION:
                editType = EDIT_TYPE.DURATION;
                break;
            case ENUM:
                editType = EDIT_TYPE.ENUM;
                break;
            case MULTI_ENUM:
                editType = EDIT_TYPE.MULTI_ENUM;
                break;
            case ENTITY:
                editType = EDIT_TYPE.ENTITY;
                break;
            case LIST:
                editType = EDIT_TYPE.MULTI_ENTITY;
                break;
            default:
                break;
        }
        return editType;
    }

    public static Object getValue(BaseObject entity, Property property) {
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

    public static void setValue(BaseObject entity, Property property, Object value) {
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
            synchronized (EntityUtils.class) {
                if (defaultMapper.get() == null) {
                    defaultMapper.set(createEntityMapper());
                }
            }
        }
        return defaultMapper.get();
    }

    public static <T extends BaseObject> void copyIgnoreNull(T src, T target) {
        if (src.getClass() != target.getClass()) {
            return;
        }

        List<Property> properties = AppUtils.getBean(PropertyService.class).get(src.getClass());
        for (Property property : properties) {
            Object srcValue = getValue(src, property);
            if (property.getType() == PROPERTY_TYPE.ENTITY) {
                if (srcValue != null) {
                    final long relationId = ((BaseObject) srcValue).getId();
                    if (relationId < 0) {
                        continue;
                    }
                    srcValue = ServiceManager.fetchService((Class<? extends BaseObject>) property.getTypeClass()).getById(relationId);
                }
            } else if (srcValue == null) {
                continue;
            }

            setValue(target, property, srcValue);
        }
    }

    public static void cloneEntity(BaseObject origin, BaseObject target) {
        List<Property> properties = AppUtils.getBean(PropertyService.class).get(origin.getClass());
        for (Property property : properties) {
            if (property.getPropertyCode().equals("id")) {
                continue;
            }
            if (property.getType() == PROPERTY_TYPE.LIST) {
                continue;
            }
            Object entityValue = EntityUtils.getValue(origin, property);
            EntityUtils.setValue(target, property, entityValue);
        }
    }

    public static String getPrimaryValue(BaseObject entity) {
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

    public static <T extends BaseObject> boolean isAll(Collection<T> entities, Predicate<T> fun) {
        for (T entity : entities) {
            if (!fun.test(entity)) {
                return false;
            }
        }
        return !entities.isEmpty();
    }

    public static <T extends BaseObject> boolean isAny(Collection<T> entities, Predicate<T> fun) {
        for (T entity : entities) {
            if (fun.test(entity)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends BaseObject> boolean isNone(Collection<T> entities, Predicate<T> fun) {
        return !isAny(entities, fun);
    }

    public static <T extends BaseObject> void sort(List<T> entities, Comparator<T> c) {
        entities.sort((o1, o2) -> {
            int value = c.compare(o1, o2);
            return value != 0 ? value : o1.compareTo(o2);
        });
    }
}
