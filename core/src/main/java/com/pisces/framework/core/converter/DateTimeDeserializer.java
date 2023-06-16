package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.util.Date;

/**
 * 日期时间串并转换器
 *
 * @author Jason
 * @date 2022/12/07
 */
public class DateTimeDeserializer extends BaseDeserializer<Date> {

    @Override
    public Date deserialize(Property property, String value) {
        return DateUtils.parse(value);
    }
}
