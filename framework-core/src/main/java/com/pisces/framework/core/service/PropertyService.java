package com.pisces.framework.core.service;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.Property;

import java.util.List;

/**
 * 字段属性service
 *
 * @author jason
 * @date 2022/12/07
 */
public interface PropertyService extends BeanService<Property> {
    /**
     * 得到
     *
     * @param beanClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> get(Class<? extends BeanObject> beanClass);

    /**
     * 得到
     *
     * @param beanClass 实体类
     * @param code        代码
     * @return {@link Property}
     */
    Property get(Class<? extends BeanObject> beanClass, String code);

    /**
     * 得到初选
     *
     * @param beanClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> getPrimaries(Class<? extends BeanObject> beanClass);

    /**
     * 得到界面
     *
     * @param beanClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> getForUi(Class<? extends BeanObject> beanClass);

    /**
     * 得到流行ui
     *
     * @param beanClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> getForPopUi(Class<? extends BeanObject> beanClass);
}
