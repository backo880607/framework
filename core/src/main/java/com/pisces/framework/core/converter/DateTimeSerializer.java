package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.util.Date;

/**
 * 日期时间序列化器
 *
 * @author Jason
 * @date 2022/12/07
 */
public class DateTimeSerializer extends BaseSerializer<Date> {

    @Override
    public String serialize(Date value) {
        return DateUtils.format(value);
    }
}