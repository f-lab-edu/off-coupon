package com.flab.offcoupon.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static com.flab.offcoupon.util.LockMagicNumber.LOCK_LEASE_MILLI_SECOND;
import static com.flab.offcoupon.util.LockMagicNumber.LOCK_WAIT_MILLI_SECOND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DistributeLockExecutorWithRedissonTest {

    @Mock
    RedissonClient redissonClient;

    @Mock
    RLock lock;

    private DistributeLockExecutorWithRedisson distributeLockExecutorWithRedisson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        distributeLockExecutorWithRedisson = new DistributeLockExecutorWithRedisson(redissonClient);
    }

    @Test
    @DisplayName("[SUCCESS] : 락 획득 및 반납 성공")
    void execute_lock_acquire_and_release_success() throws InterruptedException {
        String lockName = "redisson_lock";

        // RedissonClient 및 RLock Mock 객체 설정
        when(redissonClient.getLock(lockName)).thenReturn(lock);
        when(lock.tryLock(LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, TimeUnit.MILLISECONDS)).thenReturn(true);
        // lock이 이미 보유되어 있다고 가정
        when(lock.isLocked()).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        // 테스트 대상 메서드 호출
        distributeLockExecutorWithRedisson.execute(lockName, LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND,
                () -> System.out.println("쿠폰 발행 로직 수행"));

        // lock 메서드가 호출되었는지 확인
        verify(redissonClient, atLeastOnce()).getLock(lockName);
        // tryLock 메서드가 호출되었는지 확인
        verify(lock, atLeastOnce()).tryLock(LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, TimeUnit.MILLISECONDS);
        // unlock 메서드가 호출되었는지 확인(락을 보유하고 있거나 현재 스레드가 락을 보유하고 있는 경우)
        verify(lock, atLeastOnce()).unlock();
    }
    @Test
    @DisplayName("[ERROR] : 락 획득 실패 시 Exception 발생")
    void execute_acquire_lock_failure() throws InterruptedException {
        String lockName = "redisson_lock";

        // RedissonClient 및 RLock Mock 객체 설정
        when(redissonClient.getLock(lockName)).thenReturn(lock);
        when(lock.tryLock(LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, TimeUnit.MILLISECONDS)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                distributeLockExecutorWithRedisson.execute(lockName, LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND,
                        () -> System.out.println("쿠폰 발행 로직을 실행합니다")));
        assertEquals("Redisson 락 획득 실패 [ " + lockName + " ]", exception.getMessage());

        verify(redissonClient, atLeastOnce()).getLock(lockName);
        verify(lock, atLeastOnce()).tryLock(LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, TimeUnit.MILLISECONDS);
        verify(lock, never()).unlock();
    }

    @Test
    @Disabled("InterruptedException을 발생시키지 않는 버그")
    @DisplayName("[EROOR] : catch 블록 예외 처리 확인")
    void execute_catch_block_exception() throws InterruptedException {
        String lockName = "redisson_lock";

        // RedissonClient 및 RLock 모의 객체 설정
        when(redissonClient.getLock(lockName)).thenReturn(lock);
        when(lock.tryLock(LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, TimeUnit.MILLISECONDS)).thenThrow(InterruptedException.class);

        assertThrows(InterruptedException.class, () -> {
            distributeLockExecutorWithRedisson.execute(lockName, LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND,
                    () -> System.out.println("쿠폰 발행 로직을 실행합니다"));
        });
        // lock 메서드가 적어도 한 번 호출되었는지 확인합니다.
        verify(redissonClient, atLeastOnce()).getLock(lockName);
        // tryLock 메서드가 적어도 한 번 호출되었는지 확인합니다.
        verify(lock, atLeastOnce()).tryLock(LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, TimeUnit.MILLISECONDS);
        // unlock 메서드가 호출되지 않았는지 확인합니다.
        verify(lock, never()).unlock();
    }
}