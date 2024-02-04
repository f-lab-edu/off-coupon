package com.flab.offcoupon.util.bcrypt;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.Assert;

import java.util.Objects;

import static com.flab.offcoupon.exception.ErrorMessage.HASHED_PSWD_MUST_NOT_EMPTY;
import static com.flab.offcoupon.exception.ErrorMessage.PSWD_MUST_NOT_EMPTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BCryptPasswordEncryptor {

    public static String encrypt(String password) {
        Assert.hasText(password, PSWD_MUST_NOT_EMPTY);
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean match(String password, String hashedPassword) {
        Assert.hasText(password, PSWD_MUST_NOT_EMPTY);
        Assert.hasText(hashedPassword, HASHED_PSWD_MUST_NOT_EMPTY);
        return BCrypt.checkpw(password, hashedPassword);
    }
}
