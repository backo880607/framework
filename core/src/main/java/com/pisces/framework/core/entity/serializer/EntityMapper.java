package com.pisces.framework.core.entity.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.entity.serializer.EntityDeserializerModifier;
import com.pisces.framework.core.entity.serializer.EntitySerializerModifier;
import com.pisces.framework.core.enums.PROPERTY_TYPE;
import com.pisces.framework.core.utils.lang.EntityUtils;

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

    public <T extends BaseObject> void registerSerializer(Class<T> clazz, BaseSerializer<?> serializer) {
        this.serModifier.clazzSerializers.put(clazz, (BaseSerializer<Object>) serializer);
    }

    public void registerSerializer(PROPERTY_TYPE type, BaseSerializer<?> serializer) {
        this.serModifier.typeSerializers.put(type, (BaseSerializer<Object>) serializer);
    }

    public <T extends BaseObject> void registerSerializer(Class<T> clazz, String fieldName, BaseSerializer<?> serializer) {
        Map<String, BaseSerializer<Object>> beanSerializers = this.serModifier.fieldSerializers.computeIfAbsent(clazz, k -> new HashMap<>(16));
        beanSerializers.put(fieldName, (BaseSerializer<Object>) serializer);
    }

    public <T extends BaseObject> void registerDeserializer(Class<T> clazz, BaseDeserializer<?> deserializer) {
        this.desModifier.clazzDeserializers.put(clazz, (BaseDeserializer<Object>) deserializer);
    }

    public void registerDeserializer(PROPERTY_TYPE type, BaseDeserializer<?> deserializer) {
        this.desModifier.typeDeserializers.put(type, (BaseDeserializer<Object>) deserializer);
    }

    public <T extends BaseObject> void registerDeserializer(Class<T> clazz, String fieldName, BaseDeserializer<?> deserializer) {
        Map<String, BaseDeserializer<Object>> entityDeserializers = this.desModifier.fieldDeserializers.computeIfAbsent(clazz, k -> new HashMap<>(16));
        entityDeserializers.put(fieldName, (BaseDeserializer<Object>) deserializer);
    }

    public String getTextValue(BaseObject entity, Property property) {
        Object value = EntityUtils.getValue(entity, property);
        if (value == null) {
            return "";
        }

        return this.serModifier.serialize(property, value);
    }

    public void setTextValue(BaseObject entity, Property property, String text) {
        EntityUtils.setValue(entity, property, convertTextValue(property, text));
    }

    public Object convertTextValue(Property property, String text) {
        return this.desModifier.deserialize(property, text);
    }
}
