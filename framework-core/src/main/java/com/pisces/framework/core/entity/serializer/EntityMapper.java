package com.pisces.framework.core.entity.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.type.PROPERTY_TYPE;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体映射器
 *
 * @author jason
 * @date 2022/12/07
 */
public class EntityMapper extends ObjectMapper {
    private static final long serialVersionUID = 415052758487294871L;

    private final EntitySerializerModifier serModifier = new EntitySerializerModifier();

    private final EntityDeserializerModifier desModifier = new EntityDeserializerModifier();

    public void bind() {
        setSerializerFactory(getSerializerFactory().withSerializerModifier(this.serModifier));
    }

    public <T extends BeanObject> void registerSerializer(Class<T> clazz, BaseSerializer<?> serializer) {
        this.serModifier.clazzSerializers.put(clazz, (BaseSerializer<Object>) serializer);
    }

    public void registerSerializer(PROPERTY_TYPE type, BaseSerializer<?> serializer) {
        this.serModifier.typeSerializers.put(type, (BaseSerializer<Object>) serializer);
    }

    public <T extends BeanObject> void registerSerializer(Class<T> clazz, String fieldName, BaseSerializer<?> serializer) {
        Map<String, BaseSerializer<Object>> beanSerializers = this.serModifier.fieldSerializers.computeIfAbsent(clazz, k -> new HashMap<>(16));
        beanSerializers.put(fieldName, (BaseSerializer<Object>) serializer);
    }

    public <T extends BeanObject> void registerDeserializer(Class<T> clazz, BaseDeserializer<?> deserializer) {
        this.desModifier.clazzDeserializers.put(clazz, (BaseDeserializer<Object>) deserializer);
    }

    public void registerDeserializer(PROPERTY_TYPE type, BaseDeserializer<?> deserializer) {
        this.desModifier.typeDeserializers.put(type, (BaseDeserializer<Object>) deserializer);
    }

    public <T extends BeanObject> void registerDeserializer(Class<T> clazz, String fieldName, BaseDeserializer<?> deserializer) {
        Map<String, BaseDeserializer<Object>> entityDeserializers = this.desModifier.fieldDeserializers.computeIfAbsent(clazz, k -> new HashMap<>(16));
        entityDeserializers.put(fieldName, (BaseDeserializer<Object>) deserializer);
    }

    public String getTextValue(BeanObject entity, Property property) {
        Object value = ObjectUtils.getValue(entity, property);
        if (value == null) {
            return "";
        }

        return this.serModifier.serialize(property, value);
    }

    public void setTextValue(BeanObject entity, Property property, String text) {
        ObjectUtils.setValue(entity, property, convertTextValue(property, text));
    }

    public Object convertTextValue(Property property, String text) {
        return this.desModifier.deserialize(property, text);
    }
}
