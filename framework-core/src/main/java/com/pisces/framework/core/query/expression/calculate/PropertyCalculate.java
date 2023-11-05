package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.config.CoreMessage;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.exception.ExpressionException;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.type.PROPERTY_TYPE;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PropertyCalculate implements Calculate {
    private Property property = null;
    private final List<Property> paths = new ArrayList<>();
    private boolean list = false;

    @Override
    public int parse(String str, int index) {
        return parse(str, index, null);
    }

    public int parse(String str, int index, Class<? extends BeanObject> propertyClazz) {
        final int origin = index;
        int temp = index;

        PropertyService propertyService = AppUtils.getBean(PropertyService.class);
        this.property = null;
        this.paths.clear();
        this.list = false;
        while (index < str.length()) {
            char curChar = str.charAt(index);
            if (curChar == '.') {
                String name = str.substring(temp, index);
                if (propertyClazz == null) {
                    propertyClazz = ObjectUtils.fetchBeanClass(name);
                    if (propertyClazz == null) {
                        throw new ExpressionException(temp, index, CoreMessage.InvalidObjectName, name);
                    }
                } else {
                    Property path = propertyService.get(propertyClazz, name);
                    if (path == null) {
                        throw new ExpressionException(temp, index, CoreMessage.InvalidProperty, propertyClazz.getName(), name);
                    }
                    if (path.getType() != PROPERTY_TYPE.BEAN && path.getType() != PROPERTY_TYPE.LIST) {
                        throw new ExpressionException(temp, index, CoreMessage.NotEntityOrList, propertyClazz.getName(), name);
                    }
                    this.paths.add(path);
                    propertyClazz = (Class<? extends BeanObject>) path.getTypeClass();
                    if (path.getType() == PROPERTY_TYPE.LIST) {
                        this.list = true;
                    }
                }

                temp = index + 1;
            } else if (!Character.isAlphabetic(curChar) && !Character.isDigit(curChar) && curChar != '_') {
                break;
            }

            ++index;
        }

        if (temp < index) {
            String name = str.substring(temp, index);
            this.property = propertyService.get(propertyClazz, name);
            if (this.property == null) {
                throw new ExpressionException(temp, index, CoreMessage.InvalidProperty, propertyClazz.getName(), name);
            }

            return index;
        }

        throw new ExpressionException(temp, index, CoreMessage.ExpressionError, str.substring(origin));
    }

    @Override
    public Class<?> getReturnClass() {
        return null;
    }
}
