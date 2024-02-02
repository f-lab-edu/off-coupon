package com.flab.offcoupon.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.MessageFormat;

import static com.flab.offcoupon.exception.ErrorMessage.*;

/**
 * @Password 어노테이션 구현체
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 13;
    private boolean isNull = false;

    @Override
    public void initialize(Password phone) {
        this.isNull = phone.isNull();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if(validateIfNull(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(PSWD_MUST_NOT_EMPTY)
                    .addConstraintViolation();
            return false;
        }

        boolean isValidLength = validatePasswordLength(password);
        if (!isValidLength) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageFormat.format(CHECK_REQUEST_PSWD_LENGTH, MIN_SIZE, MAX_SIZE))
                    .addConstraintViolation();
            return false;
        }

        boolean isValidFormat = validateLowerCaseAndDigit(password);
        if (!isValidFormat) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(CHECK_REQUEST_PSWD_FORMAT)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validatePasswordLength(String password) {
        return !(password.length() < MIN_SIZE || password.length() > MAX_SIZE);
    }

    private boolean validateLowerCaseAndDigit(String password) {
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        for (char ch : password.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }
        return  (hasLowerCase && hasDigit);
    }

    private boolean validateIfNull(String password) {
        return password == null;
    }
}
