package com.pisces.framework.language.service.impl;

import com.pisces.framework.core.locale.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 语言服务impl
 *
 * @author jason
 * @date 2022/12/07
 */
@Component
public class LanguageServiceImpl implements LanguageService {
    @Autowired
    private MessageSource messageSource;
    private final ThreadLocal<Locale> localeContext = new ThreadLocal<>();

    public static final String ERROR = "#error#";

    @Override
    public Locale getLocale() {
        return localeContext.get();
    }

    @Override
    public void setLocale(Locale locale) {
        if (locale == null) {
            return;
        }
        Locale currentLocale = localeContext.get();
        if (currentLocale != null && currentLocale.equals(locale)) {
            return;
        }
        localeContext.remove();
        localeContext.set(locale);
    }

    private String getImpl(String key, Object... arguments) {
        try {
            return messageSource.getMessage(key, arguments, getLocale());
        } catch (NoSuchMessageException ignored) {
        }
        return key;
    }

    private String getFieldImpl(String key) {
        try {
            return messageSource.getMessage(key, null, getLocale());
        } catch (NoSuchMessageException ignored) {
        }
        return "";
    }

    @Override
    public String get(Enum<?> key, Object... arguments) {
        if (key == null) {
            return ERROR;
        }
        return getImpl(key.getDeclaringClass().getSimpleName() + "." + key.name(), arguments);
    }

    @Override
    public String get(Class<?> clazz, Enum<?> key, Object... arguments) {
        if (key == null) {
            return ERROR;
        }
        return getImpl(clazz.getSimpleName() + "." + key.name(), arguments);
    }

    @Override
    public String get(String key, Object... arguments) {
        if (StringUtils.isEmpty(key)) {
            return ERROR;
        }
        return getImpl(key, arguments);
    }

    @Override
    public String get(Class<?> entityClass) {
        if (entityClass == null) {
            return ERROR;
        }
        return getImpl(entityClass.getSimpleName());
    }

    @Override
    public String get(Field field) {
        if (field == null) {
            return ERROR;
        }
        return get(field.getDeclaringClass(), field.getName());
    }

    @Override
    public String get(Class<?> entityClass, String field) {
        if (entityClass == null) {
            return ERROR;
        }
        final String key = entityClass.getSimpleName() + "." + field;
        String lang = getFieldImpl(key);
//        if (lang.isEmpty() && BaseObject.class.isAssignableFrom(entityClass) && entityClass != EntityObject.class) {
//            Class<?> supperClass = EntityUtils.getSuperClass((Class<? extends EntityObject>) entityClass);
//            if (supperClass != null) {
//                return get(supperClass, field);
//            }
//        }
        return lang.isEmpty() ? key : lang;
    }

    @Override
    public String getTips(Class<?> entityClass) {
        if (entityClass == null) {
            return ERROR;
        }
        return getImpl("tips." + entityClass.getSimpleName());
    }

    @Override
    public String getTips(Field field) {
        if (field == null) {
            return ERROR;
        }
        return getImpl("tips" + field.getDeclaringClass().getSimpleName() + "." + field.getName());
    }
}