package com.flab.offcoupon.controller;

import com.flab.offcoupon.service.CouponIssueFacade;
import com.flab.offcoupon.service.CouponIssueService;
import com.flab.offcoupon.service.OptimisticLockFacade;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
@RestController
public class CouponIssueController {

    private final CouponIssueFacade couponIssueFacade;
    private final OptimisticLockFacade optimisticLockFacade;
    private final CouponIssueService couponIssueService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/issues")
    public ResponseEntity<ResponseDTO> issue(@PathVariable final long eventId,
                                             @RequestParam final long couponId,
                                             @RequestParam final long memberId) throws Exception {
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 29, 13, 0, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId));
    }
}
