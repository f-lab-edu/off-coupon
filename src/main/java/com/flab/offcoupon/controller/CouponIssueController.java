package com.flab.offcoupon.controller;

import com.flab.offcoupon.service.couponIssue.CouponIssueFacade;
import com.flab.offcoupon.util.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/api/v1/event")
@RestController
public class CouponIssueController {

    private final CouponIssueFacade couponIssueFacade;
    @Autowired
    public CouponIssueController(@Qualifier("redissonLockCouponIssue") CouponIssueFacade couponIssueFacade) {
        this.couponIssueFacade = couponIssueFacade;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/issues")
    public ResponseEntity<ResponseDTO> issue(@PathVariable final long eventId,
                                             @RequestParam final long couponId,
                                             @RequestParam final long memberId) throws InterruptedException {
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 29, 13, 0, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueFacade.issueCoupon(currentDateTime, eventId, couponId, memberId));
    }
}
