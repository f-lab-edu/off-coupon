package com.flab.offcoupon.component;

import com.flab.offcoupon.component.lock.DistributeLockExecutorWithNamedLock;
import com.flab.offcoupon.repository.mysql.NamedLockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DistributeLockExecutorWithNamedLockTest {
    @Mock
    private NamedLockRepository namedLockRepository;

    private DistributeLockExecutorWithNamedLock distributeLockExecutorWithNamedLock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        distributeLockExecutorWithNamedLock = new DistributeLockExecutorWithNamedLock(namedLockRepository);
    }

    @Test
    @DisplayName("[SUCCESS] : 락 획득 및 반납 성공")
    void execute_lock_get_and_release_success() {
        String lockName = "namedLock";

        // 락 획득과 반납이 성공하도록 설정
        when(namedLockRepository.getLock(lockName)).thenReturn(1);
        when(namedLockRepository.releaseLock(lockName)).thenReturn(1);

        // 테스트 대상 메서드 호출
        distributeLockExecutorWithNamedLock.execute(lockName,
                () -> System.out.println("쿠폰 발행 로직 수행"));

        // lock 메서드가 호출되었는지 확인
        verify(namedLockRepository, atLeastOnce()).getLock(lockName);
        // unlock 메서드가 호출되었는지 확인
        verify(namedLockRepository, atLeastOnce()).releaseLock(lockName);
    }

    @Test
    @DisplayName("[ERROR] : 락 획득 실패 Exception 후 반납 시도")
    void execute_lock_get_failure() {
        String lockName = "namedLock";

        when(namedLockRepository.getLock(lockName)).thenReturn(0);
        when(namedLockRepository.releaseLock(lockName)).thenReturn(1);

        // 테스트 대상 메서드 호출
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                distributeLockExecutorWithNamedLock.execute(lockName, () -> System.out.println("쿠폰 발행 로직 수행")));
        assertEquals("NamedLock 락 획득 실패 [ " + lockName + " ]", exception.getMessage());

        // lock 메서드가 호출되었는지 확인
        verify(namedLockRepository, atLeastOnce()).getLock(lockName);
        // unlock 메서드가 호출되었는지 확인 : 락 획득 실패해도 finally로 락 반납 시도
        verify(namedLockRepository, atLeastOnce()).releaseLock(lockName);
    }

    @Test
    @DisplayName("[ERROR] : 락 반납 실패 Exception")
    void execute_lock_release_failure() {
        String lockName = "namedLock";

        when(namedLockRepository.getLock(lockName)).thenReturn(1);
        when(namedLockRepository.releaseLock(lockName)).thenReturn(0);

        // 테스트 대상 메서드 호출
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                distributeLockExecutorWithNamedLock.execute(lockName,
                        () -> System.out.println("쿠폰 발행 로직 수행")));
        assertEquals("NamedLock 락 반납 실패 [ " + lockName + " ]", exception.getMessage());
        // lock 메서드가 호출되었는지 확인
        verify(namedLockRepository, atLeastOnce()).getLock(lockName);
        // unlock 메서드가 호출되었는지 확인
        verify(namedLockRepository, atLeastOnce()).releaseLock(lockName);
    }
}