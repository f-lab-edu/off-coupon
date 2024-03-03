package com.flab.offcoupon.service;

import com.flab.offcoupon.repository.NamedLockRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueFacade {

    private final CouponIssueService couponIssueService;
    private final NamedLockRepository namedLockRepository;
    @Transactional
    public ResponseDTO issueRequestV1(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws Exception {
        ResponseDTO responseDTO;
        try {
            int namedLock = namedLockRepository.getLock("namedLock");
            log.info("getLock = {}", namedLock);
            responseDTO = couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        } finally {
            int releaseLock = namedLockRepository.releaseLock("namedLock");
            log.info("releaseLock = {}", releaseLock);
        }
        log.info("락 반납. 쿠폰 발급 완료. memberId : {}, couponId : {}", memberId, couponId);
        return responseDTO;
    }
}
