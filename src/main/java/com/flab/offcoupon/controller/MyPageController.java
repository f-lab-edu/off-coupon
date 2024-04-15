package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.response.AllCouponsByMemberIdResponse;
import com.flab.offcoupon.service.mypage.MyPageService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/my-page")
@RestController
public class MyPageController {

    private final MyPageService myPageService;
    @GetMapping("/coupons")
    public ResponseEntity<ResponseDTO<List<AllCouponsByMemberIdResponse>>> getAllCoupons(@RequestParam final long memberId) {
        return ResponseEntity.status(HttpStatus.OK).body(myPageService.getAllCoupons(memberId));
    }
}
