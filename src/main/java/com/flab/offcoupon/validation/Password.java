package com.flab.offcoupon.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface Password {
    String message() default "please input a password.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
