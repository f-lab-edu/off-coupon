package com.flab.offcoupon.dto.request;

import com.flab.offcoupon.validation.Password;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import static com.flab.offcoupon.exception.ErrorMessage.CHECK_REQUEST_EMAIL;
import static com.flab.offcoupon.exception.ErrorMessage.EMAIL_MUST_NOT_EMPTY;

@Getter
public final class LoginMemberRequestDto {

    @NotBlank(message = EMAIL_MUST_NOT_EMPTY)
    @Email(message = CHECK_REQUEST_EMAIL)
    private final String email;

    @Password
    private final String password;

    private LoginMemberRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginMemberRequestDto create(String email, String password) {
        return new LoginMemberRequestDto(email, password);
    }
}
