package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.dto.response.SignupMemberResponseDto;
import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody @Valid final SignupMemberRequestDto signupMemberRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signUp(signupMemberRequestDto));
    }
}
