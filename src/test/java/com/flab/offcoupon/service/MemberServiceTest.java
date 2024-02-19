package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.Role;
import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.repository.MemberRepository;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static com.flab.offcoupon.exception.ErrorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;
    @Test
    @DisplayName("[SUCCESS] 회원가입 성공")
    void singup_success() {
        // given
        SignupMemberRequestDto validSignupMemberRequestDto = SignupMemberRequestDto.create("seijin0722@naver.com", "abcabc123", "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        doNothing().when(memberRepository).save(any());
        ResponseDTO responseDTO = memberService.signUp(validSignupMemberRequestDto);
        assertThat(responseDTO.getData()).isNotNull();
    }

    @Test
    @DisplayName("[ERROR] 회원가입 실패 : 중복 이메일 입력")
    void singup_fail_duplicated_email() {
        // given
        SignupMemberRequestDto duplicateSignupMemberRequestDto = SignupMemberRequestDto.create("seijin0722@naver.com", "abcabc123", "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);

        // when 이미 해당 이메일이 존재한다고 가정
        when(memberRepository.existMemberByEmail(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> memberService.signUp(duplicateSignupMemberRequestDto))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessageStartingWith(DUPLICATED_EMAIL);
    }
}