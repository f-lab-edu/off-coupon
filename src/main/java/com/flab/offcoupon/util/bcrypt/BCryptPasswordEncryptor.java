package com.flab.offcoupon.util.bcrypt;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptPasswordEncryptor {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean match(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
