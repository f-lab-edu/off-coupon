package com.flab.offcoupon.util.bcrypt;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.flab.offcoupon.exception.ErrorMessage.PSWD_MUST_NOT_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BCryptPasswordEncryptorTest {

    @Test
    @DisplayName("[ERROR] 비밀번호가 null 일때 암호화 실패")
    void encrypt_fail_null_password() {
        String password = null;
        assertThatThrownBy(() -> BCryptPasswordEncryptor.encrypt(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(PSWD_MUST_NOT_EMPTY);
    }

    @Test
    @DisplayName("[SUCCESS] 비밀번호 암호화 성공")
    void encrypt_success() {
        String password = "abcabc123";
        assertThat(BCryptPasswordEncryptor.encrypt(password)).isNotNull();
    }
}