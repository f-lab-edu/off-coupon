package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.service.CouponIssueService;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/issues")
@RestController
public class CouponIssueController {

    private final CouponIssueService couponIssueService;
    @PostMapping("/{eventId}")
    public ResponseEntity<ResponseDTO> issue(@PathVariable final long eventId,
                                              @RequestParam final long couponId,
                                              @RequestParam final long memberId) {
        return ResponseEntity.ok(couponIssueService.issueCoupon(eventId, couponId, memberId));
    }
}
