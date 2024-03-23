package com.flab.offcoupon.component;

import com.flab.offcoupon.component.lock.DistributeLockExecutorWithLettuce;
import com.flab.offcoupon.repository.redis.LettuceLockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.Mockito.*;

class DistributeLockExecutorWithLettuceTest {
    private static final long HOLD_LOCK_INTERVAL = 3_000L;

    @Mock
    private LettuceLockRepository lettuceLockRepository;

    private DistributeLockExecutorWithLettuce distributeLockExecutorWithLettuce;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        distributeLockExecutorWithLettuce = new DistributeLockExecutorWithLettuce(lettuceLockRepository);
    }

    @Test
    @DisplayName("[SUCCESS] : 락 획득 성공 후 락 해제")
    void execute_get_lock_and_get_release_once() {
        long couponId = 1000L;

        // 첫 번째 호출 시 true를 반환하도록 설정
        when(lettuceLockRepository.lock(couponId, "coupon_issue")).thenReturn(true);

        // 테스트 대상 메서드 호출
        assertTimeout(Duration.ofSeconds(HOLD_LOCK_INTERVAL), () ->
                distributeLockExecutorWithLettuce.execute(couponId, () -> System.out.println("쿠폰 발행 로직 수행")));

        // lock 메서드는 한 번 호출되어야 함 ( 락 획득 성공 )
        verify(lettuceLockRepository, atLeastOnce()).lock(couponId, "coupon_issue");

        // unlock 메서드는 한 번 호출되어야 함 ( 락 반납 성공 )
        verify(lettuceLockRepository, atLeastOnce()).unlock(couponId, "coupon_issue");
    }

    @Test
    @DisplayName("[SUCCESS] : 첫 번째 시도에서 락 획득 실패 후 두 번째 시도 -> 락 획득 성공")
    void execute_get_lock_retry() {
        long couponId = 1000L;

        // 첫 번째 호출 시 false(락 획득 실패), 두 번째 호출 시 true(락 획득 성공) 반환하도록 설정
        when(lettuceLockRepository.lock(couponId, "coupon_issue")).thenReturn(false, true);

        // 테스트 대상 메서드 호출
        assertTimeout(Duration.ofSeconds(HOLD_LOCK_INTERVAL), () ->
                distributeLockExecutorWithLettuce.execute(couponId, () -> System.out.println("쿠폰 발행 로직 수행")));

        // lock 메서드는 두 번 이상 호출되어야 함 (첫 번째 시도 실패 후 재시도)
        verify(lettuceLockRepository, atLeast(2)).lock(couponId, "coupon_issue");

        // unlock 메서드는 한 번 호출되어야 함 ( 락 반납 성공 )
        verify(lettuceLockRepository, atLeastOnce()).unlock(couponId, "coupon_issue");
    }

    /**
     * timeout이 발생한 것을 성공한 것으로 간주하고 테스트를 종료합니다.
     * @See <a href="https://stackoverflow.com/questions/26741710/how-to-get-a-success-with-junit-and-timeout">How to get a success with JUnit and timeout</a>
     * @See <a href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions">JUnit5 User Guide</a>
     */
    @Test
    @DisplayName("[ERROR] : 락 획득 실패 시 타임아웃 발생")
    void execute_get_lock_timeout() {
        long couponId = 1000L;

        // 락 획득 시 false 반환하도록 설정
        when(lettuceLockRepository.lock(couponId, "coupon_issue")).thenReturn(false);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    distributeLockExecutorWithLettuce.execute(couponId, () -> System.out.println("쿠폰 발행 로직 수행"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // lock 메서드는 두 번 이상 호출되어야 함 (첫 번째 시도 실패 후 재시도)
                verify(lettuceLockRepository, atLeast(2)).lock(couponId, "coupon_issue");
                // unlock 메서드는 호출되지 않아야 함 (락 획득 실패)
                verify(lettuceLockRepository, never()).unlock(couponId, "coupon_issue");

            }
        };
        thread.start();
    }
}