package com.pisces.framework.core.validator.impl;

import com.pisces.framework.core.validator.Validator;
import com.pisces.framework.core.validator.constraints.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机验证器
 *
 * @author jason
 * @date 2022/12/08
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Validator.isMobile(s);
    }
}
