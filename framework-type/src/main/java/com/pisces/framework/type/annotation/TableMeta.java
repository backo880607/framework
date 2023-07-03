package com.pisces.framework.type.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表元
 *
 * @author Jason
 * @date 2022/12/07
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableMeta {
    /**
     * 表名
     *
     * @return {@link String}
     */
    String name() default "";

    /**
     * 用户是否可以自定义字段，默认不允许.
     *
     * @return false: 不允许，true: 允许
     */
    boolean customize() default false;

    /**
     * 只读
     *
     * @return boolean
     */
    boolean readOnly() default false;

    /**
     * 用户是否可以进行导入数据配置，默认不允许
     *
     * @return false: 不允许，true: 允许
     */
    boolean importable() default false;

    /**
     * 用户是否可以进行导出数据配置，默认不允许
     *
     * @return false: 不允许，true: 允许
     */
    boolean exportable() default false;
}
