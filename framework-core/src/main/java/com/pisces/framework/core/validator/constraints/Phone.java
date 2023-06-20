package com.pisces.framework.core.validator.constraints;

import com.pisces.framework.core.validator.impl.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 电话校验器
 *
 * @author jason
 * @date 2022/12/03
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    /**
     * 消息
     *
     * @return {@link String}
     */
    String message() default "{javax.validation.constraints.Email.message}";

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
