package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody @Valid final SignupMemberRequestDto signupMemberRequestDto) {
        return ResponseEntity.ok(memberService.signUp(signupMemberRequestDto));
    }
}
