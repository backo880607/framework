package com.pisces.framework.core.entity.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pisces.framework.core.entity.Property;

import java.io.IOException;

/**
 * 基地序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class BaseSerializer<T> extends JsonSerializer<T> {
    protected Property property;

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String text = serialize(value);
        gen.writeString(text != null ? text : "");
    }

    /**
     * 序列化
     *
     * @param value 价值
     * @return {@link String}
     */
    protected abstract String serialize(T value);
}
