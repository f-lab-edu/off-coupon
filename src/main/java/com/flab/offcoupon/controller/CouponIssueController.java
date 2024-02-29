package com.flab.offcoupon.controller;

import com.flab.offcoupon.service.CouponIssueService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class CouponIssueController {

    private final CouponIssueService couponIssueService;

    @PostMapping("/{eventId}/issues")
    public ResponseEntity<ResponseDTO> issue(@PathVariable final long eventId,
                                             @RequestParam final long couponId,
                                             @RequestParam final long memberId) {
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 29, 13, 0, 0);
        return ResponseEntity.ok(couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId));
    }
}
