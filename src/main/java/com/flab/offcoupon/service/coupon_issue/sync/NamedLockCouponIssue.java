package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.component.lock.DistributeLockExecutorWithNamedLock;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NamedLockCouponIssue는 CouponIssueFacade 인터페이스를 구현한 쿠폰 발급 서비스입니다.
 * <p>NamedLock은 MySQL의 GET_LOCK, RELEASE_LOCK명령어를 사용하여 락을 구현할 수 있습니다.</p>
 * 네임드락은 아래 2가지 방법 모두 가능합니다.
 * <ol>
 *     <li> 트랜잭션 외부로 락의 위치 선정</li>
 *     <li> 동시성 이슈가 발생하는 부분만 별도로 트랜잭션 분리</li>
*  </ol>
 *
 *  하지만 2번째 방법의 경우 동시에 요청 시 트랜잭션이 2배로 발생하기 때문에 단일 데이터베이스에서는 커넥션 풀을 늘려줘야 합니다.
 *  커넥션 풀을 무작정 늘려도 성능저하가 발생하기 때문에 현재 프로젝트에서는 1번째 방법을 사용하고 있습니다.
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
    public ResponseDTO<String> issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        AtomicReference<ResponseDTO<String>> responseDTO = new AtomicReference<>();
        // distributeLockExecutorWithNamedLock를 사용하여 락을 획득합니다.
        distributeLockExecutorWithNamedLock.execute("namedLock", () ->
            // 락을 획득한 후, 실제 쿠폰 발급 서비스를 호출합니다.
            responseDTO.set(defaultCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId)));
        // 쿠폰 발급 결과를 반환합니다.
        return responseDTO.get();
    }
}
