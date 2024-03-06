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
    /**
     * 쿠폰 발급 시 발생할 수 있는 부정합에 대해 다양한 락킹 기법을 사용했습니다.
     * 그 중에서 Redisson의 pub/sub기반인 RLock을 사용했습니다.
     */
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
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueFacade.issueCoupon(currentDateTime, eventId, couponId, memberId));
    }
}
