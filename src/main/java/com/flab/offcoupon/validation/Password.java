package com.flab.offcoupon.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.*;

import static com.flab.offcoupon.exception.ErrorMessage.PSWD_MUST_NOT_EMPTY;


@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface Password {
    String message() default PSWD_MUST_NOT_EMPTY;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
