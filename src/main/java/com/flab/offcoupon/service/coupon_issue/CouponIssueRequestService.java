package com.flab.offcoupon.service.coupon_issue;

import com.flab.offcoupon.dto.request.IssueRequestParameter;
import com.flab.offcoupon.service.coupon_issue.async.AsyncCouponIssueService;
import com.flab.offcoupon.service.coupon_issue.sync.*;
import com.flab.offcoupon.util.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CouponIssueRequestService {
    private final CouponIssueFacade couponIssueFacade;
    private final AsyncCouponIssueService asyncCouponIssueService;
    /**
     * 쿠폰 발급 시 발생할 수 있는 부정합 이슈를 해결하고자 다양한 락킹 기법을 사용했습니다.
     * <p>전략패턴을 사용해 Load Time 시점에 선택할 수 있도록 구조화 했습니다.</p>
     * CouponIssueFacade를 구현한 클래스는 아래와 같습니다.
     * <ul>
     *     <li> {@link PessimisticLockCouponIssue} </li>
     *     <li> {@link NamedLockCouponIssue} </li>
     *     <li> {@link LettuceLockCouponIssue} </li>
     *     <li> {@link RedissonLockCouponIssue} </li>
     * </ul>
     * 현재 프로젝트에서는 Redisson의 pub/sub기반인 RLock을 사용해서 락을 구현할 수 있도록 의존성을 주입했습니다.
     */

    @Autowired
    public CouponIssueRequestService(@Qualifier("redissonLockCouponIssue") CouponIssueFacade couponIssueFacade,
                                     AsyncCouponIssueService asyncCouponIssueService) {
        this.couponIssueFacade = couponIssueFacade;
        this.asyncCouponIssueService = asyncCouponIssueService;
    }

    /**
     * 쿠폰 발급 요청을 동기식으로 처리합니다.
     *
     * @param currentDateTime 현재 시각
     * @param requestParameter 쿠폰 발급 요청 파라미터
     * @return 응답 DTO
     */
    public ResponseDTO<String> syncIssueCoupon(LocalDateTime currentDateTime, IssueRequestParameter requestParameter) throws InterruptedException {
        return couponIssueFacade.issueCoupon(currentDateTime, requestParameter);
    }

    /**
     * 쿠폰 발급 요청을 비동기식으로 처리합니다.
     *
     * @param currentDateTime 현재 시각
     * @param requestParameter 쿠폰 발급 요청 파라미터
     * @return 응답 DTO
     */
    public ResponseDTO<String> asyncIssueCoupon(LocalDateTime currentDateTime, IssueRequestParameter requestParameter) {
        return asyncCouponIssueService.issueCoupon(currentDateTime, requestParameter);
    }
}
