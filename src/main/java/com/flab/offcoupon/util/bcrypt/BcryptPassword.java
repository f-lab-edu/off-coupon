package com.flab.offcoupon.util.bcrypt;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BcryptPassword {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean decrypt(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
