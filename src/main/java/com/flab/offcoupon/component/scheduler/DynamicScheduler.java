package com.flab.offcoupon.component.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 동적 스케줄러(ThreadPoolTaskScheduler)를 생성하는 컴포넌트 클래스입니다.<br>
 * 스케줄러를 시작하거나 종료할 수 있습니다.<br>
 * <p>
 * 동적 스케줄링을 구현한 이유는 @Scheduled 로 작성된 스케줄러의 경우 여러 디렉토리가 분산되어 있다면 동료 개발자에게 혼동을 줄 수 있기 때문입니다.
 * </p>
 */
@RequiredArgsConstructor
public class DynamicScheduler {
    private ThreadPoolTaskScheduler scheduler;
    private final Runnable runnable;
    private final Trigger trigger;

    public void stopScheduler() {
        scheduler.shutdown();
    }

    public void startScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        // 스케줄러가 시작되는 부분
        scheduler.schedule(runnable, trigger);
    }
}