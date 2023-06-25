package com.pisces.framework.core.converter;

import com.pisces.framework.core.entity.serializer.BaseSerializer;
import com.pisces.framework.core.locale.LocaleManager;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.type.MultiEnum;

/**
 * 多enum序列化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class MultiEnumSerializer extends BaseSerializer<MultiEnum<? extends Enum<?>>> {

    @Override
    public String serialize(MultiEnum<? extends Enum<?>> value) {
        return StringUtils.join(value.getEnumList(), ";", temp -> LocaleManager.getLanguage(property.getTypeClass(), temp));
    }
}
