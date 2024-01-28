package com.flab.offcoupon.controller.api;

import com.flab.offcoupon.exception.member.MemberBadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.util.regex.Pattern;

import static com.flab.offcoupon.exception.Constant.*;

@Getter
@NoArgsConstructor
public class MemberMapperDTO {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String birthDate;
    @NotBlank
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
            throw new MemberBadRequestException(CHECK_REQUEST_EMAIL);
        }
        return email;
    }

    public String validatePhone(String phone) {
        String pattern = "^\\d{3}-\\d{3,4}-\\d{4}$";
        if (!Pattern.matches(pattern,phone)) {
            throw new MemberBadRequestException(CHECK_REQUEST_PHONE);
        }
        return phone;
    }

    public String validateBirthDate(String birthDate) {
        String pattern = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!Pattern.matches(pattern, birthDate)) {
            throw new MemberBadRequestException(CHECK_REQUEST_BIRTHDATE);
        }
        return birthDate;
    }

    public String validatePassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*\\d).{8,13}$";
        if (!Pattern.matches(pattern, password)) {
            throw new MemberBadRequestException(CHECK_REQUEST_PASSWORD);
        }
        return password;
    }
}
