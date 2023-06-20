package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.util.Date;

/**
 * 时间序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class TimeSerializer extends BaseSerializer<Date> {

    @Override
    public String serialize(Date value) {
        return DateUtils.format(value, DateUtils.TIME_PATTERN);
    }
}
