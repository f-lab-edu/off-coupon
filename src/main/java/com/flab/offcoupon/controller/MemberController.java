package com.flab.offcoupon.controller;

import com.flab.offcoupon.domain.Member;
import com.flab.offcoupon.dto.request.LoginMemberRequestDto;
import com.flab.offcoupon.dto.request.MemberMapperDTO;
import com.flab.offcoupon.dto.response.LoginMemberResponseDto;
import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
import com.flab.offcoupon.util.SessionManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    private final SessionManager sessionManager;
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody @Valid final MemberMapperDTO memberMapperDTO) {
        return ResponseEntity.ok(memberService.signUp(memberMapperDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody final LoginMemberRequestDto loginMemberRequestDto) {
        Member loginMember = memberService.login(loginMemberRequestDto);
        sessionManager.setLoginMember(loginMember.getId());
        return ResponseEntity.ok(ResponseDTO.getSuccessResult(LoginMemberResponseDto.create(loginMember)));
    }
}
