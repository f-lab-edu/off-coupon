package com.flab.offcoupon.controller.api;

import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/memebers")
public class MemberController {

    private final MemberService memberService;
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody MemberMapperDTO memberMapperDTO) {
        memberService.signUp(memberMapperDTO);
        return ResponseEntity.ok(null);
    }
}
