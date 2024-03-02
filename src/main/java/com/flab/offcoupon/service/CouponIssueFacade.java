package com.flab.offcoupon.service;

import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueFacade {

    private final CouponIssueService couponIssueService;

    public ResponseDTO issueRequestV1(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws Exception {
        log.info("락 획득");
        ResponseDTO responseDTO;
        synchronized (this) {
            responseDTO = couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        }
        log.info("락 반납. 쿠폰 발급 완료. memberId : {}, couponId : {}", memberId, couponId);
        return responseDTO;
    }
}
