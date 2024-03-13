package com.flab.offcoupon.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributeLockExecutorWithRedisson {

    private final RedissonClient redissonClient;
    /**
     * Redisson을 사용하여 분산 락을 처리하는 컴포넌트 클래스
     *
     * @param lockName            락의 이름
     * @param waitMilliSecond     락을 기다리는 최대 시간 (밀리초)
     * @param leaseMilliSecond    락의 보유 시간 (밀리초)
     * @param runnable            락 획득 후 실행할 작업
     * @throws InterruptedException 락을 기다리는 동안 인터럽트가 발생한 경우
     * @throws IllegalStateException 락 획득에 실패한 경우
     */
    public void execute(String lockName, long waitMilliSecond, long leaseMilliSecond, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            // 락을 기다리는 최대 시간(waitMilliSecond)동안 락을 획득 시도
            if (!lock.tryLock(waitMilliSecond, leaseMilliSecond, TimeUnit.MILLISECONDS)) {
                /**
                 * 락을 기다리는 동안 인터럽트가 발생한 경우 중단하기 전에 처리해야 할 정리 작업 수행
                 */
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Redisson 락 획득 실패 [ " + lockName + " ]");
            }
            // 락 획득 후 실행할 작업 실행
            runnable.run();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            /**
             * InterruptedException 오류를 제대로 처리하지 않을 경우 스레드가 인터럽트되었다는 정보가 손실될 수 있습니다.
             * 예외를 로그로 기록하는 것만으로는 충분하지 않기 때문에 예외를 무시한 것으로 간주됩니다.
             * 따라서, InterruptedException이 발생한 경우 스레드의 인터럽트 상태를 다시 설정해주어야 합니다.
             *
             * 예외 회피 : throw e;
             * 예외 전환 : Thread.currentThread().interrupt();
             */
            Thread.currentThread().interrupt();
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
