package com.flab.offcoupon.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.MessageFormat;

import static com.flab.offcoupon.exception.ErrorMessage.*;

/**
 * @Password 어노테이션 구현체
 */
public final class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 13;
    @Override
    public void initialize(Password phone) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if(validateIfNull(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(PSWD_MUST_NOT_EMPTY)
                    .addConstraintViolation();
            return false;
        }

        if (!validatePasswordLength(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageFormat.format(CHECK_REQUEST_PSWD_LENGTH, MIN_SIZE, MAX_SIZE))
                    .addConstraintViolation();
            return false;
        }

        if (!validateLowerCaseAndDigit(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(CHECK_REQUEST_PSWD_FORMAT)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validatePasswordLength(String password) {
        return MIN_SIZE <= password.length() && password.length() <= MAX_SIZE;
    }

    private boolean validateLowerCaseAndDigit(String password) {
        return password.chars().anyMatch(Character::isLowerCase) &&
                password.chars().anyMatch(Character::isDigit);
    }

    private boolean validateIfNull(String password) {
        return password == null;
    }
}
