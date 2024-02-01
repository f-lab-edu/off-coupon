package com.flab.offcoupon.controller.api;

import com.flab.offcoupon.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;


import static com.flab.offcoupon.exception.Constant.*;

@Generated
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class MemberMapperDTO {

    @NotBlank
    @Email(message = CHECK_REQUEST_EMAIL)
    private String email;

    @Password
    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern( message= CHECK_REQUEST_BIRTHDATE , regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String birthDate;

    @NotBlank
    @Pattern( message= CHECK_REQUEST_PHONE , regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private String phone;

    @Builder
    private MemberMapperDTO(String email, String password, String name, String birthDate, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public static MemberMapperDTO create(String email, String password, String name, String birthDate, String phone) {
        return MemberMapperDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .birthDate(birthDate)
                .phone(phone)
                .build();
    }
}
