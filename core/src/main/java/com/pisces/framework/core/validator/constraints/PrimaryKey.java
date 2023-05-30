package com.pisces.framework.core.validator.constraints;

import com.pisces.framework.core.validator.impl.PrimaryKeyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键校验器
 *
 * @author Jason
 * @date 2022/12/03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PrimaryKeyValidator.class})
public @interface PrimaryKey {
    /**
     * 主键字段标识
     *
     * @return {@link String[]}
     */
    String[] fields() default {};

    /**
     * 消息
     *
     * @return {@link String}
     */
    String message() default "{WebMessage.CREATE}";

    /**
     * 组
     *
     * @return {@link Class}<{@link ?}>{@link []}
     */
    Class<?>[] groups() default {};

    /**
     * 有效载荷
     *
     * @return {@link Class}<{@link ?} {@link extends} {@link Payload}>{@link []}
     */
    Class<? extends Payload>[] payload() default {};
}
