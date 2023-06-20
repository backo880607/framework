package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 反序列化器日期
 *
 * @author Jason
 * @date 2022/12/07
 */
public class DateDeserializer extends BaseDeserializer<Date> {

    @Override
    public final Date deserialize(Property property, String value) {
        try {
            return DateUtils.parse(value, DateUtils.DATE_PATTERN);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
