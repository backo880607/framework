package com.pisces.framework.core.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * 实体反序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class BeanDeserializer extends BaseDeserializer<BeanObject> {

    @Override
    public BeanObject deserialize(JsonParser p, DeserializationContext context) throws IOException {
        BeanObject entity;
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);

        String strId = null;
        JsonNode idNode = node.get("id");
        if (idNode != null) {
            strId = idNode.textValue();
            if (strId == null) {
                strId = idNode.toString();
            }
        }
        entity = deserialize(property, strId);
        if (entity.getId() > 0) {
            return entity;
        }

        PropertyService propertyService = AppUtils.getBean(PropertyService.class);
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            if (field.getValue() == null) {
                continue;
            }
            String value = field.getValue().textValue();
            if (value == null) {
                value = field.getValue().toString();
            }
            Property entityProperty = propertyService.get((Class<BeanObject>) property.getTypeClass(), field.getKey());
            if (entityProperty != null) {
                ObjectUtils.defaultObjectMapper().setTextValue(entity, entityProperty, value);
            }
        }
        return entity;
    }

    @Override
    public BeanObject deserialize(Property property, String value) {
        if (StringUtils.isEmpty(value)) {
            value = "0";
        }
        final long id = Long.parseLong(value);
        if (id > 0) {
            return ObjectUtils.getInherit((Class<BeanObject>) property.getTypeClass(), id);
        }
        BeanObject entity;
        try {
            entity =  (BeanObject) property.getTypeClass().newInstance();
            entity.setId(id);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return entity;
    }

}
