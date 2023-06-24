package com.pisces.framework.core.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.pisces.framework.core.entity.MultiEnum;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.utils.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

/**
 * 多enum串并转换器
 *
 * @author jason
 * @date 2022/12/07
 */
public class MultiEnumDeserializer extends BaseDeserializer<MultiEnum<? extends Enum<?>>> implements ContextualDeserializer {
    /**
     * clazz
     */
    private Class<? extends MultiEnum<? extends Enum<?>>> enumClass;

    @Override
    public final MultiEnum<? extends Enum<?>> deserialize(JsonParser p, DeserializationContext context)
            throws IOException {
        MultiEnum<? extends Enum<?>> result = BeanUtils.instantiateClass(enumClass);
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        String text = StringUtils.join(node.iterator(), ";", JsonNode::asText);
        result.parse(text);
        return result;
    }

    @Override
    public MultiEnum<? extends Enum<?>> deserialize(Property property, String value) {
        MultiEnum<? extends Enum<?>> result = (MultiEnum<? extends Enum<?>>) BeanUtils.instantiateClass(property.getTypeClass());
        result.parse(value);
        return result;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        Class<? extends MultiEnum<? extends Enum<?>>> rawClass = (Class<? extends MultiEnum<? extends Enum<?>>>) context.getContextualType().getRawClass();
        MultiEnumDeserializer deserializer = new MultiEnumDeserializer();
        deserializer.enumClass = rawClass;
        return deserializer;
    }
}
