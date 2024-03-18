package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.component.DistributeLockExecutorWithLettuce;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * LettuceLockCouponIssue CouponIssueFacade 인터페이스를 구현한 쿠폰 발급 서비스입니다.
 * <p>Lettuce의 SETNEX명령어를 사용하여 분산 환경에서 락을 처리할 수 있습니다.</p>
 * Spinlock 방식으로 100ms마다 락 획득 요청을 하기 때문에 Redis에 많은 부하를 줄 수 있습니다.
 * 또한 <a href="https://redis.io/commands/setnx/">Redis 공식문서 SETNX Command</a>에 따르면 해당 명령어는 2.6.12버전부터 권장하지 않습니다.
 */
@RequiredArgsConstructor
@Component
public class LettuceLockCouponIssue implements CouponIssueFacade {

    /**
     * DistributeLockExecutorWithLettuce는 Lettuce을 사용하여 락을 처리하는 컴포넌트 클래스입니다.
     * DefaultCouponIssueService는 실제 쿠폰 발급 로직을 담당하는 서비스입니다.
     **/
    private final DistributeLockExecutorWithLettuce distributeLockExecutorWithLettuce;
    private final DefaultCouponIssueService defaultCouponIssueService;

    @Override
    public ResponseDTO<String> issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {

        AtomicReference<ResponseDTO<String>> responseDTO = new AtomicReference<>();
        // distributeLockExecutorWithLettuce를 사용하여 락을 획득합니다.
        distributeLockExecutorWithLettuce.execute(couponId, () ->
            // 락을 획득한 후, 실제 쿠폰 발급 서비스를 호출합니다.
            responseDTO.set(defaultCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId)));
        // 쿠폰 발급 결과를 반환합니다.
        return responseDTO.get();
    }
}
