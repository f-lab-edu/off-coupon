package com.flab.offcoupon.controller.api;

import com.flab.offcoupon.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;


import static com.flab.offcoupon.exception.ErrorMessage.*;

@Generated
@Getter
@EqualsAndHashCode
public final class MemberMapperDTO {

    @NotBlank(message = EMAIL_MUST_NOT_EMPTY)
    @Email(message = CHECK_REQUEST_EMAIL)
    private final String email;

    @Password
    private final String password;

    @NotBlank (message = NAME_MUST_NOT_EMPTY)
    private final String name;

    @NotBlank(message = BIRTHDATE_MUST_NOT_EMPTY)
    @Pattern(message= CHECK_REQUEST_BIRTHDATE , regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private final String birthDate;

    @NotBlank(message = PHONE_MUST_NOT_EMPTY)
    @Pattern( message= CHECK_REQUEST_PHONE , regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private final String phone;

    private MemberMapperDTO(String email, String password, String name, String birthDate, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public static MemberMapperDTO create(String email, String password, String name, String birthDate, String phone) {
        return  new MemberMapperDTO (email, password, name, birthDate, phone);
    }
}