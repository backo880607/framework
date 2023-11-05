package com.pisces.framework.core.entity.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.pisces.framework.core.converter.*;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.type.PROPERTY_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体序列化器修饰符
 *
 * @author jason
 * @date 2022/12/07
 */
public class EntitySerializerModifier extends BeanSerializerModifier {
    private static final Map<PROPERTY_TYPE, BaseSerializer<?>> DEFAULT_SERIALIZERS = new HashMap<>();
    Map<Class<?>, BaseSerializer<Object>> clazzSerializers = new HashMap<>();
    Map<PROPERTY_TYPE, BaseSerializer<Object>> typeSerializers = new HashMap<>();
    Map<Class<? extends BeanObject>, Map<String, BaseSerializer<Object>>> fieldSerializers = new HashMap<>();

    static {
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.DOUBLE, new DoubleSerializer());
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.DATE, new DateSerializer());
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.TIME, new TimeSerializer());
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.DATE_TIME, new DateTimeSerializer());
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.DURATION, new DurationSerializer());
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.ENUM, new EnumSerializer());
        DEFAULT_SERIALIZERS.put(PROPERTY_TYPE.MULTI_ENUM, new MultiEnumSerializer());
//        defaultSerializers.put(PROPERTY_TYPE.ENTITY, new EntitySerializer());
//        defaultSerializers.put(PROPERTY_TYPE.LIST, new EntityListSerializer());
    }

    public BaseSerializer<Object> getSerializer(Property property) {
        if (property == null) {
            return null;
        }

        BaseSerializer<Object> serializer = null;
        Map<String, BaseSerializer<Object>> fieldSerializer = fieldSerializers.get(property.getBelongClass());
        if (fieldSerializer != null) {
            serializer = fieldSerializer.get(property.getPropertyCode());
        }
        if (serializer != null) {
            return serializer;
        }
        serializer = typeSerializers.get(property.getType());
        if (serializer != null) {
            return serializer;
        }

        Class<?> temp = property.getTypeClass();
        while (temp != Object.class) {
            serializer = clazzSerializers.get(temp);
            if (serializer != null) {
                break;
            }
            temp = temp.getSuperclass();
        }
        if (serializer != null) {
            return serializer;
        }

        return (BaseSerializer<Object>) DEFAULT_SERIALIZERS.get(property.getType());
    }

    public String serialize(Property property, Object value) {
        BaseSerializer<Object> serializer = getSerializer(property);
        return serializer != null ? serializer.serialize(value) : value.toString();
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        if (BeanObject.class.isAssignableFrom(beanDesc.getBeanClass())) {
            PropertyService propertyService = AppUtils.getBean(PropertyService.class);
            Class<BeanObject> beanClass = (Class<BeanObject>) beanDesc.getBeanClass();
            for (BeanPropertyWriter writer : beanProperties) {
                Property property = propertyService.get(beanClass, writer.getName());
                BaseSerializer<Object> serializer = getSerializer(property);
                if (serializer != null) {
                    serializer.property = property;
                    writer.assignSerializer(serializer);
                }
            }
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }

    private JsonSerializer<Object> getJacksonSerializer(PROPERTY_TYPE type) {
        JsonSerializer<Object> serializer = null;
        switch (type) {
            case BOOLEAN -> serializer = new BooleanSerializer(false);
            case SHORT -> serializer = new NumberSerializers.ShortSerializer();
            case INTEGER -> serializer = new NumberSerializers.IntegerSerializer(Integer.class);
            case LONG -> serializer = new NumberSerializers.LongSerializer(Long.class);
            case STRING -> serializer = new StringSerializer();
            default -> {
            }
        }
        return serializer;
    }
}
