package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.util.Collection;

/**
 * 实体列表序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class BeanListSerializer extends BaseSerializer<Collection<BeanObject>> {

    @Override
    public String serialize(Collection<BeanObject> value) {
        return StringUtils.join(value, ";", (BeanObject entity) -> entity.getId().toString());
    }
}
