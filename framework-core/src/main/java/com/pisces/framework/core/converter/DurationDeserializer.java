package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.type.Duration;

/**
 * 反序列化器持续时间
 *
 * @author Jason
 * @date 2022/12/07
 */
public class DurationDeserializer extends BaseDeserializer<Duration> {

    @Override
    public Duration deserialize(Property property, String value) {
        return new Duration(value);
    }
}
