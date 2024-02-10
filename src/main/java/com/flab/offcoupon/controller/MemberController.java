package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.request.LoginMemberRequestDto;
import com.flab.offcoupon.dto.request.MemberMapperDTO;
import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
import com.flab.offcoupon.util.SessionManager;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody @Valid final MemberMapperDTO memberMapperDTO) {
        return ResponseEntity.ok(memberService.signUp(memberMapperDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody final LoginMemberRequestDto loginMemberRequestDto) {
        return ResponseEntity.ok(memberService.login(loginMemberRequestDto));
    }
}
