package com.flab.offcoupon.util.bcrypt;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import static com.flab.offcoupon.exception.member.MemberErrorMessage.HASHED_PSWD_MUST_NOT_EMPTY;
import static com.flab.offcoupon.exception.member.MemberErrorMessage.PSWD_MUST_NOT_EMPTY;

public final class BCryptPasswordEncryptor implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        Assert.hasText((String) rawPassword, PSWD_MUST_NOT_EMPTY);
        return BCrypt.hashpw((String) rawPassword,BCrypt.gensalt());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        Assert.hasText((String) rawPassword, PSWD_MUST_NOT_EMPTY);
        Assert.hasText((String) rawPassword, HASHED_PSWD_MUST_NOT_EMPTY);
        return BCrypt.checkpw((String) rawPassword, encodedPassword);
    }
}
