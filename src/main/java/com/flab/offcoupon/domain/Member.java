package com.flab.offcoupon.domain;

import com.flab.offcoupon.dto.response.SignupMemberResponseDto;
import com.flab.offcoupon.util.DateTimeUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
public final class Member implements Serializable {

    @Id
    private long id;

    @NotBlank
    private final String email;

    @NotBlank
    private final String password;

    @NotBlank
    private final String name;

    @NotBlank
    private final LocalDate birthdate;

    @NotBlank
    private final String phone;

    @NotBlank
    private final Role role;

    @NotBlank
    private final LocalDateTime createdAt;

    @NotBlank
    private final LocalDateTime updatedAt;

    private Member(String email, String password, String name, LocalDate birthdate, String phone, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Member create(String email, String password, String name, LocalDate birthDate, String phone, Role role) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new Member(email, password, name, birthDate, phone, role, now, now);
    }
}
