package com.pisces.framework.core.locale;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 语言服务
 *
 * @author jason
 * @date 2022/12/07
 */
public interface LanguageService {
    /**
     * 获得语言环境
     *
     * @return {@link Locale}
     */
    Locale getLocale();

    /**
     * 设置语言环境
     *
     * @param locale 语言环境
     */
    void setLocale(Locale locale);

    /**
     * 得到
     *
     * @param key       关键
     * @param arguments 参数
     * @return {@link String}
     */
    String get(Enum<?> key, Object... arguments);

    /**
     * 用于多选枚举的多语言获取
     *
     * @param clazz     多选枚举类
     * @param key       枚举项
     * @param arguments 参数
     * @return 翻译后的语言
     */
    String get(Class<?> clazz, Enum<?> key, Object... arguments);

    /**
     * 得到
     *
     * @param key       关键
     * @param arguments 参数
     * @return {@link String}
     */
    String get(String key, Object... arguments);

    /**
     * 得到
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    String get(Class<?> entityClass);

    /**
     * 得到
     *
     * @param field 场
     * @return {@link String}
     */
    String get(Field field);

    /**
     * 得到
     *
     * @param entityClass 实体类
     * @param field       场
     * @return {@link String}
     */
    String get(Class<?> entityClass, String field);

    /**
     * 得到提示
     *
     * @param entityClass 实体类
     * @return {@link String}
     */
    String getTips(Class<?> entityClass);

    /**
     * 得到提示
     *
     * @param field 场
     * @return {@link String}
     */
    String getTips(Field field);
}
