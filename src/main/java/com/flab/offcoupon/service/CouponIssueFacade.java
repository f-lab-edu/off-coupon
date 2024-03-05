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

    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws Exception {
        ResponseDTO responseDTO = null;
        try {
            // 네임드락 획득
            int getLock = namedLockRepository.getLock("namedLock");
            log.info("getLock = {}", getLock);
            // 쿠폰발급 비즈니스 로직
            responseDTO = couponIssueService.issueCoupon(currentDateTime, eventId , couponId , memberId);
        } finally {
            // 락 해제
            int releaseLock = namedLockRepository.releaseLock("namedLock");
            System.out.println("releaseLock = " + releaseLock);
        }
        return responseDTO;
    }
}
