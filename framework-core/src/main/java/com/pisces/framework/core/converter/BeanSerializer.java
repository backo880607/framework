package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.ObjectUtils;

/**
 * 实体序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class BeanSerializer extends BaseSerializer<BeanObject> {

    @Override
    public String serialize(BeanObject value) {
        return ObjectUtils.getPrimaryValue(value);
    }

}
