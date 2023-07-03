package com.pisces.framework.type.annotation;

import com.pisces.framework.type.EDIT_TYPE;
import com.pisces.framework.type.PROPERTY_TYPE;
import com.pisces.framework.type.RelationKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性元
 *
 * @author jason
 * @date 2022/12/07
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyMeta {
    /**
     * false表示字段不为属性
     *
     * @return boolean
     */
    boolean property() default true;

    /**
     * true表示字段为属性，但页面不展示，仅内部使用
     *
     * @return boolean
     */
    boolean system() default false;

    /**
     * 类型
     *
     * @return {@link PROPERTY_TYPE}
     */
    PROPERTY_TYPE type() default PROPERTY_TYPE.NONE;

    /**
     * 种类
     *
     * @return {@link RelationKind}
     */
    RelationKind kind() default RelationKind.NONE;

    /**
     * 控制属性是否允许修改，若不允许修改，则只能系统进行修改
     * 若只允许部分用户修改，则通过权限控制。
     *
     * @return boolean
     */
    boolean modify() default true;

    /**
     * 隐藏
     *
     * @return boolean
     */
    boolean hide() default false;

    /**
     * 流行显示
     *
     * @return boolean
     */
    boolean popDisplay() default false;

    /**
     * 编辑类型
     *
     * @return {@link EDIT_TYPE}
     */
    EDIT_TYPE editType() default EDIT_TYPE.NONE;

    /**
     * 是否为长度超大的数据类型
     *
     * @return boolean
     */
    boolean large() default false;
}
