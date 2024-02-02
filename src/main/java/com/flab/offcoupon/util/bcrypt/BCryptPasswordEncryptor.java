package com.flab.offcoupon.util.bcrypt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BCryptPasswordEncryptor {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean match(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
