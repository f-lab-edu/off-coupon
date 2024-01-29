package com.flab.offcoupon.controller.api;

import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
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

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody @Valid final MemberMapperDTO memberMapperDTO) {
        ResponseDTO responseDTO = memberService.signUp(memberMapperDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
