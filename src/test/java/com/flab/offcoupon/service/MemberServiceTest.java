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
        SignupMemberRequestDto validSignupMemberRequestDto = SignupMemberRequestDto.create("seijin0722@naver.com", "abcabc123", "name", "2024-12-12", "010-1234-1234", Role.ROLE_USER);
        doNothing().when(memberRepository).save(any());
        ResponseDTO responseDTO = memberService.signUp(validSignupMemberRequestDto);
        assertThat(responseDTO.getData()).isEqualTo(validSignupMemberRequestDto);
    }

    @Test
    @DisplayName("[ERROR] 비밀번호가 null 일때 암호화 실패")
    void signup_fail_with_null_password() {
        SignupMemberRequestDto inValidSignupMemberRequestDto = SignupMemberRequestDto.create("seijin0722@naver.com", null, "name", "2024-12-12", "010-1234-1234", Role.ROLE_USER);
        assertThatThrownBy(() -> memberService.signUp(inValidSignupMemberRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(PSWD_MUST_NOT_EMPTY);
    }

    @Test
    @DisplayName("[ERROR] 회원가입 실패 : 중복 이메일 입력")
    void singup_fail_duplicated_email() {
        // given
        SignupMemberRequestDto duplicateSignupMemberRequestDto = SignupMemberRequestDto.create("seijin0722@naver.com", "abcabc123", "name", "2024-12-12", "010-1234-1234", Role.ROLE_USER);

        // when 이미 해당 이메일이 존재한다고 가정
        when(memberRepository.existMemberByEmail(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> memberService.signUp(duplicateSignupMemberRequestDto))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessageStartingWith(DUPLICATED_EMAIL);
    }

//    @Test // TODO : 제대로 작동안함, DB에 있어도 계속 MemberNotFoundException 발생
//    @DisplayName("[SUCCESS] 로그인 성공")
//    void login_success() {
//        LoginMemberRequestDto loginMemberRequestDto = LoginMemberRequestDto.create("sejin@email.com", "ababab123123");
//        Member member = memberService.login(loginMemberRequestDto);
//        assertThat(member).isNotNull();
//        assertThat(member).isInstanceOf(Member.class);
//    }
//
//    @Test
//    @DisplayName("[ERROR] 로그인 실패 : DB에 저장되지 않은 이메일로 로그인 시도 시")
//    void login_fail_with_invalid_email() {
//        String invalidEmail = "test@email.com";
//        assertThatThrownBy(() ->  memberService.findMemberByEmail(invalidEmail))
//                .isInstanceOf(MemberNotFoundException.class)
//                .hasMessageStartingWith(NOT_EXIST_EMAIL);
//    }
//
//    @Test
//    @DisplayName("[ERROR] 로그인 실패 : 암호화 비밀번호랑 일치하지 않을경우")
//    void login_fail_with_invalid_password() {
//        String invalidPWSD = "1111aaaa";
//        LoginMemberRequestDto loginMemberRequestDto = LoginMemberRequestDto.create("sejin@email.com", invalidPWSD);
//        assertThatThrownBy(() ->  memberService.login(loginMemberRequestDto))
//                .isInstanceOf(MemberNotFoundException.class)
//                .hasMessageStartingWith(NOT_EXIST_EMAIL);
//    }
}