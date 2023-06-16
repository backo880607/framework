package com.pisces.framework.core.service;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.entity.Property;

import java.util.List;

/**
 * 字段属性service
 *
 * @author jason
 * @date 2022/12/07
 */
public interface PropertyService extends BaseService<Property> {
    /**
     * 得到
     *
     * @param objectClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> get(Class<? extends BaseObject> objectClass);

    /**
     * 得到
     *
     * @param objectClass 实体类
     * @param code        代码
     * @return {@link Property}
     */
    Property get(Class<? extends BaseObject> objectClass, String code);

    /**
     * 得到初选
     *
     * @param objectClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> getPrimaries(Class<? extends BaseObject> objectClass);

    /**
     * 得到界面
     *
     * @param objectClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> getForUi(Class<? extends BaseObject> objectClass);

    /**
     * 得到流行ui
     *
     * @param objectClass 实体类
     * @return {@link List}<{@link Property}>
     */
    List<Property> getForPopUi(Class<? extends BaseObject> objectClass);
}
