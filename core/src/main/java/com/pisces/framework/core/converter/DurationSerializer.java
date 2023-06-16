package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.Duration;
import com.pisces.framework.core.entity.serializer.BaseSerializer;

/**
 * 持续时间序列化器
 *
 * @author Jason
 * @date 2022/12/07
 */
public class DurationSerializer extends BaseSerializer<Duration> {

    @Override
    public String serialize(Duration value) {
        return value.toString();
    }

}
