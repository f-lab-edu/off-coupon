package com.flab.offcoupon.service.couponIssue.sync;

import com.flab.offcoupon.component.DistributeLockExecutorWithNamedLock;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NamedLockCouponIssue는 CouponIssueFacade 인터페이스를 구현한 쿠폰 발급 서비스입니다.
 * NamedLock을 사용하여 분산 환경에서 안전하게 락을 처리합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NamedLockCouponIssue implements CouponIssueFacade {

    /**
     * DistributeLockExecutorWithNamedLock은 NamedLock을 사용하여 락을 처리하는 컴포넌트 클래스입니다.
     * DefaultCouponIssueService는 실제 쿠폰 발급 로직을 담당하는 서비스입니다.
     */
    private final DistributeLockExecutorWithNamedLock distributeLockExecutorWithNamedLock;
    private final DefaultCouponIssueService defaultCouponIssueService;
    @Override
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        AtomicReference<ResponseDTO> responseDTO = new AtomicReference<>();
        // distributeLockExecutorWithNamedLock를 사용하여 락을 획득합니다.
        distributeLockExecutorWithNamedLock.execute("namedLock", () -> {
            // 락을 획득한 후, 실제 쿠폰 발급 서비스를 호출합니다.
            responseDTO.set(defaultCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId));
        });
        // 쿠폰 발급 결과를 반환합니다.
        return responseDTO.get();
    }
}
