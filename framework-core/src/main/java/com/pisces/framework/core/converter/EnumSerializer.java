package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseSerializer;

/**
 * enum序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class EnumSerializer extends BaseSerializer<Enum<?>> {
    @Override
    public String serialize(Enum<?> value) {
        return value.name();
    }
}
