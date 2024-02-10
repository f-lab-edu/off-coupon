package com.flab.offcoupon.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
public final class Member {

    @Id
    private long id;

    @NotBlank
    private final String email;

    @NotBlank
    private final String password;

    @NotBlank
    private final String name;

    @NotBlank
    private final String birthdate;

    @NotBlank
    private final String phone;

    @NotBlank
    private final LocalDateTime createdAt;

    @NotBlank
    private final LocalDateTime updatedAt;

    private Member(String email, String password, String name, String birthdate, String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Member create(String email, String password, String name, String birthDate, String phone) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(email, password, name, birthDate, phone, now, now);
    }
}
