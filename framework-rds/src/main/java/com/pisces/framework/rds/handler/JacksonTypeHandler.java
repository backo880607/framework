package com.pisces.framework.rds.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.util.Objects;

/**
 * 杰克逊类型处理程序
 *
 * @author jason
 * @date 2023/07/15
 */
public class JacksonTypeHandler extends AbstractJsonTypeHandler {
    public JacksonTypeHandler(Class<?> classType) {
        super(classType);
    }

    @Override
    protected Object parse(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        try {
            return ObjectUtils.defaultBeanMapper().readValue(jsonStr, classType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    protected String toJson(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return ObjectUtils.defaultBeanMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to json string failed!" + obj, e);
        }
    }
}
