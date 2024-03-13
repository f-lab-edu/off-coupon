package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.util.ResponseDTO;

import java.time.LocalDateTime;

/**
 * CouponIssueFacade는 동기식으로 쿠폰을 발급할때 사용하는 인터페이스입니다.
 * <p>해당 인터페이스를 구현한 클래스는 아래와 같습니다.</p>
 * <ul>
 *     <li> {@link PessimisticLockCouponIssue} </li>
 *     <li> {@link NamedLockCouponIssue} </li>
 *     <li> {@link LettuceLockCouponIssue} </li>
 *     <li> {@link RedissonLockCouponIssue} </li>
 * </ul>
 * 각 클래스는 다양한 락킹 기법을 사용하여 쿠폰 발급 요청에 대한 부정합을 방지합니다.
 * 각 락 방법에 대한 설명은 기술 블로그에 작성했으니 참고해주세요.
 * @see <a href="https://strong-park.tistory.com/entry/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89%EC%97%90-%EB%8C%80%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC-1-synchronized-pessimisti-Lock-optimistic-Lock">[쿠폰 발급에 대한 동시성 처리(1) - synchronized, pessimistic Lock, optimistic Lock]</a>
 * @see <a href="https://strong-park.tistory.com/entry/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89%EC%97%90-%EB%8C%80%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC-2-MySQL%EC%9D%98-NamedLock-Redis%EC%9D%98-%EB%B6%84%EC%82%B0%EB%9D%BDLettuce-Redisson">[쿠폰 발급에 대한 동시성 처리(2) - MySQL의 NamedLock, Redis의 분산락(Lettuce, Redisson)]</a>
 */
public interface CouponIssueFacade {
    ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException;
}
