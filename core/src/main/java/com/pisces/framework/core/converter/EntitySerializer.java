package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.utils.lang.EntityUtils;

/**
 * 实体序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class EntitySerializer extends BaseSerializer<BaseObject> {

    @Override
    public String serialize(BaseObject value) {
        return EntityUtils.getPrimaryValue(value);
    }

}
