package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 时间串并转换器
 *
 * @author jason
 * @date 2022/12/07
 */
public class TimeDeserializer extends BaseDeserializer<Date> {

    @Override
    public Date deserialize(Property property, String value) {
        try {
            return DateUtils.parse(value, DateUtils.TIME_PATTERN);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
