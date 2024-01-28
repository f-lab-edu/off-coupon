package com.flab.offcoupon.controller.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberMapperDTOTest {

    private MemberMapperDTO memberMapperDTO;
    @BeforeEach
    public void init() {
        memberMapperDTO = new MemberMapperDTO();
    }

    @ParameterizedTest
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 이메일 포맷이 틀린 경우")
    @ValueSource(strings = {"@naver", "email", "123@naver", "123#email.com", "123"})
    public void validateEmail(String email) {
        assertThatThrownBy(() -> memberMapperDTO.validateEmail(email))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @ParameterizedTest
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 비밀번호 포맷이 틀린 경우")
    @ValueSource(strings = {"1234", "bcdefg","abcdefg1234567890"})
    public void validatePassword(String password) {
        assertThatThrownBy(() -> memberMapperDTO.validatePassword(password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 전화번호 포맷이 틀린 경우")
    @ValueSource(strings = {"01012341234"})
    public void validatePhone(String phone) {
        assertThatThrownBy(() -> memberMapperDTO.validatePhone(phone))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 생년월일 포맷이 틀린 경우")
    @ValueSource(strings = {"20241212","1234-556-177","12-12-12"})
    public void validateBirthDate(String birthDate) {
        assertThatThrownBy(() -> memberMapperDTO.validateBirthDate(birthDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원가입 시 유효성 검사 : 포맷 전부다 맞을 경우")
    public void validateAllFormat() {
        try {
            memberMapperDTO = new MemberMapperDTO("1234@email.com", "ascbc1234", "gildong", "2024-12-12", "010-1234-1234");
            assertThat(memberMapperDTO).isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}