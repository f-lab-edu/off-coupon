package com.flab.offcoupon.service.coupon_issue;

import com.flab.offcoupon.service.coupon_issue.async.AsyncCouponIssueService;
import com.flab.offcoupon.service.coupon_issue.sync.*;
import com.flab.offcoupon.util.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CouponIssueRequestService {
    /**
     * 쿠폰 발급 시 발생할 수 있는 부정합에 대해 다양한 락킹 기법을 사용했습니다.
     * <p>CouponIssueFacade는 동기식으로 쿠폰을 발급할때 사용하는 인터페이스입니다.</p>
     * CouponIssueFacade를 구현한 클래스는 아래와 같습니다.
     * <ul>
     *     <li> {@link PessimisticLockCouponIssue} </li>
     *     <li> {@link NamedLockCouponIssue} </li>
     *     <li> {@link LettuceLockCouponIssue} </li>
     *     <li> {@link RedissonLockCouponIssue} </li>
     * </ul>
     * 현재 프로젝트에서는 Redisson의 pub/sub기반인 RLock을 사용해서 락을 구현할 수 있도록 의존성을 주입했습니다.
     *
     * <p>AsyncCouponIssueService은 비동기식으로 쿠폰을 발급할때 사용하는 클래스입니다.</p>
     * API서버에서 쿠폰 발급 요청만 처리한다면 문제가 없겠지만, 다른 API와 병행시 처리량이 낮아지거나 MySQL이 트랜잭션을 처리할때 부하가 걸릴 수 있습니다.
     * 따라서 현재 서버에서는 API 쿠폰 발급 요청을 받기만 한 뒤, Redis에서 해당 요청에 대한 검증을 처리합니다.
     * 이후 별도의 쿠폰 발급 서버에서 Redis 대기 목록에 있는 쿠폰 발급 대상에 대한 MySQL트랜잭션을 처리합니다
     */
    private final CouponIssueFacade couponIssueFacade;
    private final AsyncCouponIssueService asyncCouponIssueService;

    @Autowired
    public CouponIssueRequestService(@Qualifier("redissonLockCouponIssue") CouponIssueFacade couponIssueFacade,
                                     AsyncCouponIssueService asyncCouponIssueService) {
        this.couponIssueFacade = couponIssueFacade;
        this.asyncCouponIssueService = asyncCouponIssueService;
    }

    public ResponseDTO<String> syncIssueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        return couponIssueFacade.issueCoupon(currentDateTime, eventId, couponId, memberId);
    }

    public ResponseDTO<String> asyncIssueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) {
        return asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
    }
}
