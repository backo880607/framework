package com.pisces.framework.core.validator.impl;

import com.pisces.framework.core.entity.Duration;
import com.pisces.framework.core.validator.ValidDuration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 时间验证器
 *
 * @author jason
 * @date 2022/12/08
 */
public class DurationValidator implements ConstraintValidator<ValidDuration, Duration> {
    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        return duration.valid();
    }
}
