package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseDeserializer;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.sql.Date;

/**
 * sql日期串并转换器
 *
 * @author jason
 * @date 2022/12/07
 */
public class SqlDateDeserializer extends BaseDeserializer<Date> {

    @Override
    public Date deserialize(Property property, String value) {
        return new Date(DateUtils.parse(value).getTime());
    }
}
