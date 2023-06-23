package com.pisces.framework.language.service.impl;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.EnumDto;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.enums.PROPERTY_TYPE;
import com.pisces.framework.core.service.BeanServiceImpl;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.language.dao.PropertyDao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 物业服务impl
 *
 * @author jason
 * @date 2022/12/07
 */
@Service
class PropertyServiceImpl extends BeanServiceImpl<Property, PropertyDao> implements PropertyService {

    @Override
    public List<Property> get(Class<? extends BeanObject> beanClass) {
        return getDao().get(beanClass);
    }

    @Override
    public Property get(Class<? extends BeanObject> beanClass, String code) {
        return getDao().get(beanClass, code);
    }

    @Override
    public List<Property> getPrimaries(Class<? extends BeanObject> beanClass) {
        return getDao().getPrimaries(beanClass);
    }

    @Override
    public List<Property> getForUi(Class<? extends BeanObject> beanClass) {
        List<Property> result = new ArrayList<>();
        List<Property> properties = get(beanClass);
//        EntityUtils.sort(properties, Comparator.comparing(Property::getOrderNumber));
        properties.removeIf(Property::getSystem);
        for (Property property : properties) {
            Property newProperty = create();
            ObjectUtils.cloneEntity(property, newProperty);
            if (newProperty.getPropertyName().isEmpty()) {
                newProperty.setPropertyName(lang.get(beanClass, newProperty.getPropertyCode()));
            }

            if (property.getEnumClass() != null) {
                List<EnumDto> enumDtoList = new ArrayList<>();
                Enum<?>[] enumItems = property.getEnumClass().getEnumConstants();
                for (Enum<?> item : enumItems) {
                    EnumDto dto = new EnumDto();
                    dto.setCode(item.name());
                    dto.setLabel(property.getType() == PROPERTY_TYPE.MULTI_ENUM ? lang.get(property.getTypeClass(), item) : lang.get(item));
                    enumDtoList.add(dto);
                }
                newProperty.setEnumItems(enumDtoList);
            }
            result.add(newProperty);
        }
        return result;
    }

    @Override
    public List<Property> getForPopUi(Class<? extends BeanObject> beanClass) {
        List<Property> result = new ArrayList<>();
        List<Property> properties = getForUi(beanClass);
        for (Property property : properties) {
            if (property.getPopDisplay()) {
                result.add(property);
            }
        }

        return result;
    }
}
