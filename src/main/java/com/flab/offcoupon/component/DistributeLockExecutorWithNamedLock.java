package com.flab.offcoupon.component;

import com.flab.offcoupon.repository.mysql.NamedLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributeLockExecutorWithNamedLock {

    private final NamedLockRepository namedLockRepository;

    /**
     * MySQL의 NamedLock를 사용하여 분산 락을 처리하는 컴포넌트 클래스
     *
     *  이 컴포넌트는 NamedLockRepository를 통해 락을 획득하고 반납하는 역할을 수행합니다.
     *  락을 획득한 후에 주어진 작업을 실행하고, 작업이 완료된 후에는 락을 반납합니다.
     *
     * @param lockName 락의 이름
     * @param runnable 락을 획득한 후 실행할 작업을 정의한 람다 표현식
     * @throws InterruptedException 락을 기다리는 동안 인터럽트가 발생한 경우
     */
    public void execute(String lockName, Runnable runnable)  {

        try {
            getLock(lockName);
            // 락 획득 후 실행할 작업 실행
            runnable.run();
        } finally {
            // 락 해제
            releaseLock(lockName);
        }
    }
    /**
     * 주어진 락의 이름으로 락을 획득합니다.
     *
     * @param lockName 획득할 락의 이름
     * @throws IllegalStateException NamedLock 획득 실패 시 발생하는 예외
     */
    private void getLock(String lockName) {
        Integer resultGetLock = namedLockRepository.getLock(lockName);
        if(resultGetLock == 0 || resultGetLock == null) {
            throw new IllegalStateException("NamedLock 락 획득 실패 [ " + lockName + " ]");
        }
    }
    /**
     * 주어진 락의 이름으로 락을 반납합니다.
     *
     * @param lockName 반납할 락의 이름
     * @throws IllegalStateException NamedLock 반납 실패 시 발생하는 예외
     */
    private void releaseLock(String lockName) {
        Integer resultReleaseLock = namedLockRepository.releaseLock(lockName);
        if(resultReleaseLock == 0 || resultReleaseLock == null) {
            throw new IllegalStateException("NamedLock 락 반납 실패 [ " + lockName + " ]");
        }
    }
}