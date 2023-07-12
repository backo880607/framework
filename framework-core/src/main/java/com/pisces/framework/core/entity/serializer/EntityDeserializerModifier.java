package com.pisces.framework.core.entity.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.pisces.framework.core.converter.*;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.type.PROPERTY_TYPE;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 实体反序列化器修饰符
 *
 * @author jason
 * @date 2022/12/07
 */
public class EntityDeserializerModifier extends BeanDeserializerModifier {
    private static final Map<PROPERTY_TYPE, BaseDeserializer<?>> DEFAULT_DESERIALIZERS = new HashMap<>();
    Map<Class<?>, BaseDeserializer<Object>> clazzDeserializers = new HashMap<>();
    Map<PROPERTY_TYPE, BaseDeserializer<Object>> typeDeserializers = new HashMap<>();
    Map<Class<? extends BeanObject>, Map<String, BaseDeserializer<Object>>> fieldDeserializers = new HashMap<>();

    static {
        DEFAULT_DESERIALIZERS.put(PROPERTY_TYPE.DATE, new DateDeserializer());
        DEFAULT_DESERIALIZERS.put(PROPERTY_TYPE.TIME, new TimeDeserializer());
        DEFAULT_DESERIALIZERS.put(PROPERTY_TYPE.DATE_TIME, new DateTimeDeserializer());
        DEFAULT_DESERIALIZERS.put(PROPERTY_TYPE.DURATION, new DurationDeserializer());
        DEFAULT_DESERIALIZERS.put(PROPERTY_TYPE.MULTI_ENUM, new MultiEnumDeserializer());
    }

    public BaseDeserializer<Object> getDeserializer(Property property) {
        if (property == null) {
            return null;
        }

        BaseDeserializer<Object> deserializer = null;
        Map<String, BaseDeserializer<Object>> fieldDeserializer = fieldDeserializers.get(property.getBelongClass());
        if (fieldDeserializer != null) {
            deserializer = fieldDeserializer.get(property.getPropertyCode());
        }
        if (deserializer != null) {
            return deserializer;
        }

        deserializer = typeDeserializers.get(property.getType());
        if (deserializer != null) {
            return deserializer;
        }

        Class<?> temp = property.getTypeClass();
        while (temp != Object.class) {
            deserializer = clazzDeserializers.get(temp);
            if (deserializer != null) {
                break;
            }
            temp = temp.getSuperclass();
        }
        if (deserializer != null) {
            return deserializer;
        }

        return (BaseDeserializer<Object>) DEFAULT_DESERIALIZERS.get(property.getType());
    }

    public Object deserialize(Property property, String text) {
        BaseDeserializer<?> deserializer = getDeserializer(property);
        return deserializer != null ? deserializer.deserialize(property, text) : deserializeDefault(property, text);
    }

    private Object deserializeDefault(Property property, String text) {
        switch (property.getType()) {
            case BOOLEAN -> {
                return Boolean.valueOf(text);
            }
            case SHORT -> {
                return Short.valueOf(text);
            }
            case INTEGER -> {
                return Integer.valueOf(text);
            }
            case LONG -> {
                return Long.valueOf(text);
            }
            case DOUBLE -> {
                return Double.valueOf(text);
            }
            case ENUM -> {
                return Enum.valueOf((Class<? extends Enum>) property.getTypeClass(), text);
            }
            case STRING -> {
                return text;
            }
            default -> {
            }
        }

        throw new UnsupportedOperationException(property.getType().name() + " type not support deserialize");
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        if (!BeanObject.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return builder;
        }

        PropertyService propertyService = AppUtils.getBean(PropertyService.class);
        Class<BeanObject> beanClass = (Class<BeanObject>) beanDesc.getBeanClass();
        Iterator<SettableBeanProperty> iter = builder.getProperties();
        while (iter.hasNext()) {
            SettableBeanProperty setProperty = iter.next();
            Property property = propertyService.get(beanClass, setProperty.getName());
            BaseDeserializer<Object> deserializer = getDeserializer(property);
            if (deserializer != null) {
                BaseDeserializer<Object> copy = BeanUtils.instantiateClass(deserializer.getClass());
                BeanUtils.copyProperties(deserializer, copy);
                deserializer = copy;
                deserializer.property = property;
                setProperty = setProperty.withValueDeserializer(deserializer);
                builder.addOrReplaceProperty(setProperty, true);
            }
        }
        return builder;
    }
}
