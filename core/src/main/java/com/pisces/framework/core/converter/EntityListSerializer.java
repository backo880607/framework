package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.util.Collection;

/**
 * 实体列表序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class EntityListSerializer extends BaseSerializer<Collection<BaseObject>> {

    @Override
    public String serialize(Collection<BaseObject> value) {
        return StringUtils.join(value, ";", (BaseObject entity) -> entity.getId().toString());
    }
}
