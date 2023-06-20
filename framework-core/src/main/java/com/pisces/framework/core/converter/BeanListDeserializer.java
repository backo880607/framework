package com.pisces.framework.core.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 实体列表串并转换器
 *
 * @author jason
 * @date 2022/12/07
 */
public class BeanListDeserializer extends BaseDeserializer<Collection<BeanObject>> {
    private static final String ENTITY_SPLIT = ";";

    @SneakyThrows
    @Override
    public Collection<BeanObject> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        ObjectCodec oc = p.getCodec();
        JsonNode nodeList = oc.readTree(p);
        if (!nodeList.isArray()) {
            return null;
        }
        Collection<BeanObject> result = new ArrayList<>();
        for (JsonNode node : nodeList) {
            BeanObject entity = (BeanObject) property.getTypeClass().newInstance();
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();
                String name = field.getKey();
                if ("id".equals(name)) {
                    String value = field.getValue().textValue();
                    if (value == null) {
                        value = field.getValue().toString();
                    }
                    return deserialize(this.property, value);
                }
            }
        }
        return result;
    }

    @Override
    public Collection<BeanObject> deserialize(Property property, String value) {
        ArrayList<BeanObject> entities = new ArrayList<>();

        for (String strId : value.split(ENTITY_SPLIT)) {
            if (strId.isEmpty()) {
                continue;
            }
            final long id = Long.parseLong(strId);
            try {
                BeanObject entity = (BeanObject) property.getTypeClass().newInstance();
                entity.setId(id);
                entities.add(entity);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return entities;
    }

}
