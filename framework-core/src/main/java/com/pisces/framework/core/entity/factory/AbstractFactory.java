package com.pisces.framework.core.entity.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.service.ServiceManager;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.type.PROPERTY_TYPE;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体工厂
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public abstract class AbstractFactory {
    private final Class<? extends BeanObject> beanClass;
    String identify = "";
    AbstractFactory superFactory = null;
    List<AbstractFactory> childFactories = new ArrayList<>();

    public AbstractFactory(Class<? extends BeanObject> value) {
        this.beanClass = value;
    }

    public abstract Object getValue(BeanObject bean, Property property);

    public abstract void setValue(BeanObject bean, Property property, Object value);

    public String getPrimaryValue(BeanObject bean) {
        if (bean == null) {
            return "";
        }

        List<Property> properties = AppUtils.getBean(PropertyService.class).getPrimaries(bean.getClass());
        return StringUtils.join(properties, ",", (Property property) -> {
            Object value = getValue(bean, property);
            if (value == null) {
                return "";
            }
            try {
                return value.getClass() == String.class ? value.toString() : ObjectUtils.defaultBeanMapper().writeValueAsString(value);
            } catch (JsonProcessingException ignored) {
            }
            return "";
        });
    }

    public <T extends BeanObject> void assignBean(Map<String, Object> data, T bean) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            Property property = AppUtils.getBean(PropertyService.class).get(bean.getClass(), entry.getKey());
            if (property == null) {
                continue;
            }

            if (value instanceof String) {
                ObjectUtils.defaultBeanMapper().setTextValue(bean, property, (String) value);
            } else {
                setValue(bean, property, value);
            }
        }
    }

    public void cloneEntity(BeanObject origin, BeanObject target) {
        List<Property> properties = AppUtils.getBean(PropertyService.class).get(origin.getClass());
        for (Property property : properties) {
            if ("id".equals(property.getPropertyCode())) {
                continue;
            }
            if (property.getType() == PROPERTY_TYPE.LIST) {
                continue;
            }
            Object entityValue = getValue(origin, property);
            setValue(target, property, entityValue);
        }
    }

    public <T extends BeanObject> void copyIgnoreNull(T src, T target) {
        if (src.getClass() != target.getClass()) {
            return;
        }

        List<Property> properties = AppUtils.getBean(PropertyService.class).get(src.getClass());
        for (Property property : properties) {
            Object srcValue = getValue(src, property);
            if (property.getType() == PROPERTY_TYPE.BEAN) {
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

    public <T extends BeanObject> T convertBean(Map<String, Object> data, Class<T> beanClass) {
        T bean = ServiceManager.fetchService(beanClass).create();
        assignBean(data, bean);
        return bean;
    }

    public <T extends BeanObject> List<T> convertBeanList(List<Map<String, Object>> dataList, Class<T> beanClass) {
        List<T> beans = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            beans.add(convertBean(data, beanClass));
        }
        return beans;
    }
}
