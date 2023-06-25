package com.pisces.framework.language.dao;

import com.pisces.framework.core.config.CoreMessage;
import com.pisces.framework.core.dao.BaseDao;
import com.pisces.framework.core.dao.DaoManager;
import com.pisces.framework.core.dao.impl.DaoImpl;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.exception.ConfigurationException;
import com.pisces.framework.core.exception.PropertyException;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.language.dao.mapper.PropertyMapper;
import com.pisces.framework.type.PROPERTY_TYPE;
import com.pisces.framework.type.annotation.PropertyMeta;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 财产刀
 *
 * @author jason
 * @date 2022/12/07
 */
@Component
public class PropertyDao implements BaseDao<Property> {
    @Resource
    private PropertyMapper mapper;
    private final Map<Class<? extends BeanObject>, Map<String, Property>> DEFAULT_PROPERTIES = new ConcurrentHashMap<>();

    public PropertyDao() {
        DaoManager.register(this);
    }

    private Map<String, Property> getDefaultProperties(Class<? extends BeanObject> beanClass) {
        Map<String, Property> properties = DEFAULT_PROPERTIES.get(beanClass);
        if (properties == null) {
            synchronized (this) {
                properties = DEFAULT_PROPERTIES.get(beanClass);
                if (properties == null) {
                    properties = ObjectUtils.getDefaultProperties(beanClass);
                    for (Property property : properties.values()) {
                        fillProperty(property);
                    }
                    DEFAULT_PROPERTIES.put(beanClass, properties);
                }
            }
        }
        return properties;
    }

    @Override
    public Property get() {
        throw new UnsupportedOperationException("select one property is not allowed");
    }

    @Override
    public List<Property> list() {
        throw new UnsupportedOperationException("select all property is not allowed");
    }

    @Override
    public Property getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<Property> listByIds(List<Long> ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public boolean exist(Long id) {
        return mapper.existsById(id);
    }

    @Override
    public int insert(Property record) {
        record = fillProperty(record);
        mapper.insert(record);
        return 1;
    }

    @Override
    public int insertBatch(List<Property> recordList) {
        List<Property> properties = new ArrayList<>();
        for (Property property : recordList) {
            properties.add(fillProperty(property));
        }
        mapper.insertBatch(properties);
        return recordList.size();
    }

    @Override
    public int update(Property item) {
        Property oldItem = get(ObjectUtils.fetchBeanClass(item.getBelongName()), item.getPropertyCode());
        if (oldItem == null) {
            throw new UnsupportedOperationException("update a not existed property class:" + item.getBelongName() + " property:" + item.getPropertyCode());
        }
        if (oldItem.getInherent()) {
            throw new UnsupportedOperationException("update a inherent property class:" + item.getBelongName() + " property:" + item.getPropertyCode());
        }

        item = fillProperty(item);
        mapper.update(item);
        return 1;
    }

    @Override
    public int updateBatch(List<Property> items) {
        List<Property> properties = new ArrayList<>();
        for (Property item : items) {
            Property oldItem = get(ObjectUtils.fetchBeanClass(item.getBelongName()), item.getPropertyCode());
            if (oldItem == null) {
                throw new UnsupportedOperationException("update a not existed property class:" + item.getBelongName() + " property:" + item.getPropertyCode());
            }
            if (oldItem.getInherent()) {
                throw new UnsupportedOperationException("update a inherent property class:" + item.getBelongName() + " property:" + item.getPropertyCode());
            }
            properties.add(fillProperty(item));
        }
        mapper.updateBatch(properties);
        return items.size();
    }

    @Override
    public int delete(Property item) {
        Property oldRecord = get(ObjectUtils.fetchBeanClass(item.getBelongName()), item.getPropertyCode());
        if (oldRecord == null) {
            return 0;
        }
        if (oldRecord.getInherent()) {
            throw new UnsupportedOperationException("can`t delete a inherent property class:" + item.getBelongName() + " property:" + item.getPropertyCode());
        }

        return mapper.deleteById(oldRecord.getId());
    }

    @Override
    public int deleteBatch(List<Property> items) {
        List<Property> properties = new ArrayList<>();
        for (Property item : items) {
            Property oldItem = get(ObjectUtils.fetchBeanClass(item.getBelongName()), item.getPropertyCode());
            if (oldItem == null) {
                return 0;
            }
            if (oldItem.getInherent()) {
                throw new UnsupportedOperationException("can`t delete a inherent property class:" + item.getBelongName() + " property:" + item.getPropertyCode());
            }
            properties.add(oldItem);
        }
        return mapper.deleteBatch(properties);
    }

    @Override
    public int deleteById(Long id) {
        throw new UnsupportedOperationException("deleteById is not allowed");
    }

    @Override
    public int deleteIdBatch(List<Long> ids) {
        throw new UnsupportedOperationException("deleteByIds is not allowed");
    }

    @Override
    public DaoImpl createDaoImpl() {
        return null;
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

    private Property fillProperty(Property property) {
        if (!property.isInitialized()) {
            Property newItem = new Property();
            newItem.init();
            ObjectUtils.copyIgnoreNull(property, newItem);
            property = newItem;
        }
        if (property.getBelongClass() == null) {
            property.setBelongClass(ObjectUtils.fetchBeanClass(property.getBelongName()));
            if (property.getBelongClass() == null) {
                throw new PropertyException(CoreMessage.InvalidObjectName, property.getBelongName());
            }
        }
        Class<?> typeClass = ObjectUtils.getTypeClass(property.getType());
        if (typeClass == null) {
            if (!StringUtils.isEmpty(property.getTypeFullName())) {
                try {
                    typeClass = Class.forName(property.getTypeFullName());
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException(property.getBelongName() + "`s field " + property.getPropertyCode() + " invalid full type name: " + property.getTypeFullName());
                }
            } else if (!StringUtils.isEmpty(property.getTypeName())) {
                typeClass = ObjectUtils.fetchBeanClass(property.getTypeName());
            }
        }
        if (typeClass == null) {
            throw new ConfigurationException(property.getBelongName() + "`s field " + property.getPropertyCode() + " property type is invalid");
        }
        property.setTypeClass(typeClass);
        property.setTypeName(typeClass.getSimpleName());
        property.setTypeFullName(typeClass.getName());

//        initRelation(property);
        initGetSetMethod(property);
        initEnumConstants(property);
        if (StringUtils.isEmpty(property.getEditType())) {
            property.setEditType(ObjectUtils.getEditType(property.getType()).name());
        }
        return property;
    }

//    private void initRelation(Property property) {
//        property.setSign(Primary.get().getRelationSign(property.getBelongClass(), property.getPropertyCode()));
//        if (property.getSign() == null) {
//            return;
//        }
//
//        RelationKind kind = Primary.get().getRelationKind(property.getBelongClass(), property.getSign());
//        if (kind == null) {
//            throw new UnsupportedOperationException(property.getBelongClass().getName() + "`s field " + property.getPropertyCode() + " not set relation annotation");
//        }
//        property.setType(kind == RelationKind.SINGLETON ? PROPERTY_TYPE.ENTITY : PROPERTY_TYPE.LIST);
//
//        RelationType type = Primary.get().getRelationType(property.getBelongClass(), property.getSign());
//        if (type == RelationType.ONE_TO_MULTI) {
//            property.setModify(false);
//        }
//    }

    private void initGetSetMethod(Property property) {
        try {
            if (property.getGetMethod() == null) {
                if (property.getInherent()) {
                    property.setGetMethod(property.getBelongClass().getMethod("get" + org.springframework.util.StringUtils.capitalize(property.getPropertyCode())));
                } else {
                    property.setGetMethod(property.getBelongClass().getMethod("getUserFields", String.class));
                }
            }
        } catch (NoSuchMethodException ignored) {
        }
        boolean canWrite = false;
        if (property.getGetMethod() != null) {
            canWrite = property.getGetMethod().getAnnotation(PropertyMeta.class) == null;
        }

        try {
            if (property.getSetMethod() == null && canWrite) {
                if (property.getInherent()) {
                    if (property.getType() != PROPERTY_TYPE.LIST) {
                        property.setSetMethod(property.getBelongClass().getMethod("set" + org.springframework.util.StringUtils.capitalize(property.getPropertyCode()), property.getTypeClass()));
                    }
                } else {
                    property.setSetMethod(property.getBelongClass().getMethod("setUserFields", String.class, Object.class));
                }
            }
        } catch (NoSuchMethodException ignored) {
        }

        if (property.getGetMethod() == null) {
            throw new ConfigurationException(property.getBelongClass().getName() + "`s Field " + property.getPropertyCode() + " has not get method!");
        }
        if (property.getSetMethod() == null && canWrite && property.getType() != PROPERTY_TYPE.LIST) {
            throw new ConfigurationException(property.getBelongClass().getName() + "`s Field " + property.getPropertyCode() + " has not set method!");
        }
    }

    private void initEnumConstants(Property property) {
        if (property.getType() == PROPERTY_TYPE.ENUM) {
            property.setEnumClass((Class<Enum<?>>) property.getTypeClass());
        } else if (property.getType() == PROPERTY_TYPE.MULTI_ENUM) {
            Class<?> implCls = property.getTypeClass().getDeclaredClasses()[0];
            property.setEnumClass((Class<Enum<?>>) implCls);
        }
    }

    public List<Property> get(Class<? extends BeanObject> beanClass) {
        return new ArrayList<>();
//        Example example = new Example(Property.class);
//        example.createCriteria().andEqualTo("dataSetId", AppUtils.getDataSetId()).andEqualTo("belongName", entityClass.getSimpleName());
//        List<Property> properties = mapper.selectByExample(example);
//        Map<String, Property> defaultProperties = getDefaultProperties(beanClass);
//        if (properties == null) {
//            properties = new ArrayList<>();
//        } else {
//            for (Property property : properties) {
//                fillProperty(property);
//                defaultProperties.remove(property.getPropertyCode());
//            }
//        }
//        properties.addAll(defaultProperties.values());
//        return properties;
    }

    public Property get(Class<? extends BeanObject> beanClass, String code) {
        return null;
//        Example example = new Example(Property.class);
//        example.createCriteria().andEqualTo("dataSetId", AppUtils.getDataSetId())
//                .andEqualTo("belongName", beanClass.getSimpleName()).andEqualTo("propertyCode", code);
//        Property property = mapper.selectOneByExample(example);
//        if (property == null) {
//            property = getDefaultProperties(beanClass).get(code);
//        } else {
//            property = fillProperty(property);
//        }
//        return property;
    }

    public List<Property> getPrimaries(Class<? extends BeanObject> beanClass) {
        List<Property> result = new ArrayList<>();
        List<Property> properties = get(beanClass);
        for (Property property : properties) {
            if (property.getPrimaryKey()) {
                result.add(property);
            }
        }

        if (result.isEmpty()) {
            result.add(get(beanClass, "id"));
        }
        return result;
    }
}
