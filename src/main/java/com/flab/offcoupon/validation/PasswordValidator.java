package com.flab.offcoupon.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.MessageFormat;

import static com.flab.offcoupon.exception.Constant.CHECK_REQUEST_PSWD_FORMAT;
import static com.flab.offcoupon.exception.Constant.CHECK_REQUEST_PSWD_LENGTH;

/**
 * @Password 어노테이션 구현체
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 13;

    @Override
    public void initialize(Password phone) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValidLength = checkPasswordLength(password);
        if (!isValidLength) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageFormat.format(CHECK_REQUEST_PSWD_LENGTH, MIN_SIZE, MAX_SIZE))
                    .addConstraintViolation();
            return false;
        }

        boolean isValidFormat = checkLowerCaseAndDigit(password);
        if (!isValidFormat) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(CHECK_REQUEST_PSWD_FORMAT)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean checkPasswordLength(String password) {
        if (password.length() < MIN_SIZE || password.length() > MAX_SIZE) {
            return false;
        }
        return true;
    }

    private boolean checkLowerCaseAndDigit(String password) {
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        for (char ch : password.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }
        if (!hasLowerCase || !hasDigit) {
            return false;
        }
        return true;
    }
}
