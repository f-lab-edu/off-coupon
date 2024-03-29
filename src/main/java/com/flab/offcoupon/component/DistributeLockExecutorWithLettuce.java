package com.flab.offcoupon.component;

import com.flab.offcoupon.repository.redis.LettuceLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributeLockExecutorWithLettuce {

    private final LettuceLockRepository lettuceLockRepository;
    private static final long TRY_GET_LOCK_INTERVAL = 100L;

    /**
     * Lettuce를 사용하여 분산 락을 처리하는 컴포넌트 클래스
     *
     * @param couponId 락의 대상이 되는 쿠폰 ID
     * @param runnable 락을 획득한 후 실행할 작업을 정의한 람다 표현식
     * @throws InterruptedException 락을 기다리는 동안 인터럽트가 발생한 경우
     */
    public void execute(long couponId, Runnable runnable) throws InterruptedException {
        while (!Boolean.TRUE.equals(lettuceLockRepository.lock(couponId, "coupon_issue"))) {
            // Spin Lock: 100밀리초 동안 계속해서 락 획득을 시도합니다.
            Thread.sleep(TRY_GET_LOCK_INTERVAL);
        }
        try {
            // 락 획득 후 실행할 작업 실행
            runnable.run();
        } finally {
            // 락 해제
            lettuceLockRepository.unlock(couponId,"coupon_issue");
        }
    }
}
