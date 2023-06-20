package com.pisces.framework.core.entity.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.pisces.framework.core.entity.Property;

import java.io.IOException;

/**
 * 反序列化器基础
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class BaseDeserializer<T> extends JsonDeserializer<T> {
    protected Property property;

    @Override
    public T deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return deserialize(this.property, p.getText());
    }

    /**
     * 反序列化
     *
     * @param property 财产
     * @param value    价值
     * @return {@link T}
     */
    public abstract T deserialize(Property property, String value);
}
