package com.flab.offcoupon.controller.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
public class MemberMapperDTO {

    private String email;
    private String password;
    private String name;
    private String birthDate;
    private String phone;

    public MemberMapperDTO(String email, String password, String name, String birthDate, String phone) {
        this.email = validateEmail(email);
        this.password = validatePassword(password);
        this.name = name;
        this.birthDate = validateBirthDate(birthDate);
        this.phone = validatePhone(phone);
    }

    public String validateEmail(String email) {
        String pattern = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";
        if (!Pattern.matches(pattern,email)) {
            throw new IllegalArgumentException();
        }
        return email;
    }

    public String validatePhone(String phone) {
        String pattern = "^\\d{3}-\\d{3,4}-\\d{4}$";
        if (!Pattern.matches(pattern,phone)) {
            throw new IllegalArgumentException();
        }
        return phone;
    }

    public String validateBirthDate(String birthDate) {
        String pattern = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!Pattern.matches(pattern, birthDate)) {
            throw new IllegalArgumentException();
        }
        return birthDate;
    }

    public String validatePassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*\\d).{8,13}$";
        if (!Pattern.matches(pattern, password)) {
            throw new IllegalArgumentException();
        }
        return password;
    }
}
