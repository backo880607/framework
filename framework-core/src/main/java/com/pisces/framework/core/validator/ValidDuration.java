package com.pisces.framework.core.validator;

import com.pisces.framework.core.validator.impl.DurationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 有效时间
 *
 * @author jason
 * @date 2022/12/08
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DurationValidator.class})
public @interface ValidDuration {
    String message() default "{CoreMessage.DurationError}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
