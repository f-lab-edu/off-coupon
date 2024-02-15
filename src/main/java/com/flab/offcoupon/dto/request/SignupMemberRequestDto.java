package com.flab.offcoupon.dto.request;

import com.flab.offcoupon.domain.Role;
import com.flab.offcoupon.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


import static com.flab.offcoupon.exception.ErrorMessage.*;

@Generated
@Getter
@EqualsAndHashCode
public final class SignupMemberRequestDto {

    @NotBlank(message = EMAIL_MUST_NOT_EMPTY)
    @Email(message = CHECK_REQUEST_EMAIL)
    private final String email;

    @Password
    private final String password;

    @NotBlank (message = NAME_MUST_NOT_EMPTY)
    private final String name;

    @NotBlank(message = BIRTHDATE_MUST_NOT_EMPTY)
    @Pattern(message= CHECK_REQUEST_BIRTHDATE , regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private final String birthdate;

    @NotBlank(message = PHONE_MUST_NOT_EMPTY)
    @Pattern( message= CHECK_REQUEST_PHONE , regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private final String phone;

    @NotNull(message = ROLE_MUST_NOT_EMPTY)
    private final Role role;

    private SignupMemberRequestDto(String email, String password, String name, String birthdate, String phone,Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.role = role;
    }

    public static SignupMemberRequestDto create(String email, String password, String name, String birthDate, String phone,Role role) {
        return  new SignupMemberRequestDto(email, password, name, birthDate, phone,role);
    }
}
