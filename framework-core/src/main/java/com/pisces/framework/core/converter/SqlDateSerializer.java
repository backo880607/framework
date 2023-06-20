package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.DateUtils;

import java.sql.Date;

/**
 * sql日期序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class SqlDateSerializer extends BaseSerializer<Date> {

    @Override
    public String serialize(Date value) {
        return DateUtils.format(value);
    }

}
