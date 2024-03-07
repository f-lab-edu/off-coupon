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

    private void getLock(String lockName) {
        Integer resultGetLock = namedLockRepository.getLock(lockName);
        if(resultGetLock == 0 || resultGetLock == null) {
            throw new IllegalStateException("NamedLock 락 획득 실패 [ " + lockName + " ]");
        }
    }

    private void releaseLock(String lockName) {
        Integer resultReleaseLock = namedLockRepository.releaseLock(lockName);
        if(resultReleaseLock == 0 || resultReleaseLock == null) {
            throw new IllegalStateException("NamedLock 락 반납 실패 [ " + lockName + " ]");
        }
    }
}
