package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.params.TimeParams;
import com.flab.offcoupon.util.DateTimeUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public final class Member implements Serializable {

    @Serial
    private static final long serialVersionUID = 4074593846206721580L;
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

    @NotNull
    private final Role role;

    @NotNull
    private final LocalDateTime createdAt;

    @NotNull
    private final LocalDateTime updatedAt;

    private Member(String email, String password, String name, LocalDate birthdate, String phone, Role role, TimeParams timeParams) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.role = role;
        this.createdAt = timeParams.createdAt();
        this.updatedAt = timeParams.updatedAt();
    }

    public static Member create(String email, String password, String name, LocalDate birthDate, String phone, Role role) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new Member(email, password, name, birthDate, phone, role, new TimeParams(now, now));
    }
}
