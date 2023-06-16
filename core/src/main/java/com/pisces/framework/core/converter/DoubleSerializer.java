package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.DoubleUtils;

/**
 * 浮点数序列化，考虑精度。
 *
 * @author Jason
 * @date 2022-03-09
 */
public class DoubleSerializer extends BaseSerializer<Double> {
    @Override
    public String serialize(Double value) {
        if (value == null) {
            return "";
        }
        if (this.property == null) {
            return value.toString();
        }

        return String.valueOf(DoubleUtils.valueOf(value, property.getPrecis()));
    }
}
