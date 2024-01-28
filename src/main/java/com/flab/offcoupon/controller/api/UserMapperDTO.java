package com.flab.offcoupon.controller.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMapperDTO {

    private int memberId;
    private String email;
    private String password;
    private String name;
    private String birthDate;
    private String phone;

    public UserMapperDTO(String email, String password, String name, String birthDate, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
    }
}
