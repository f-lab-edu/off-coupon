package com.flab.offcoupon.service;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.repository.MemberMapperRepository;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.flab.offcoupon.exception.Constant.DUPLICATED_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    MemberMapperRepository memberMapperRepository;


    @Test
    @DisplayName("[SUCCESS] 회원가입 성공")
    void singup_success() {
        // given
        MemberMapperDTO validMemberMapperDTO = MemberMapperDTO.create("seijin0722@naver.com", "abcabc123", "name", "2024-12-12", "010-1234-1234");
        doNothing().when(memberMapperRepository).save(any());
        ResponseDTO responseDTO = memberService.signUp(validMemberMapperDTO);
        assertThat(responseDTO.getData()).isEqualTo(validMemberMapperDTO);
    }

    @Test
    @DisplayName("[ERROR] 회원가입 실패 : 중복 이메일 입력")
    void singup_fail_duplicated_email() {
        // given
        MemberMapperDTO duplicateMemberMapperDTO = MemberMapperDTO.create("seijin0722@naver.com", "abcabc123", "name", "2024-12-12", "010-1234-1234");

        // when 이미 해당 이메일이 존재한다고 가정
        when(memberMapperRepository.countByEmail(any())).thenReturn(1);

        // then
        assertThatThrownBy(() -> memberService.signUp(duplicateMemberMapperDTO))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessageStartingWith(DUPLICATED_EMAIL);
    }
}