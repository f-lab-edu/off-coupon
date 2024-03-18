package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;

/**
 * PessimisticLockCouponIssue CouponIssueFacade 인터페이스를 구현한 쿠폰 발급 서비스입니다.
 * <p>SELECT... FOR UPDATE을 사용해서 레코드 락 기반의 비관적 락을 구현했습니다.</p>
 **/
@RequiredArgsConstructor
@Service
@Slf4j
public class PessimisticLockCouponIssue implements CouponIssueFacade {

    private final CouponRepository couponRepository;
    private final DefaultCouponIssueService defaultCouponIssueService;
    @Transactional
    @Override
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) {
        defaultCouponIssueService.checkEventPeriodAndTime(eventId, currentDateTime);
        increaseIssuedCouponQuantity(couponId);
        defaultCouponIssueService.saveCouponIssue(memberId, couponId, currentDateTime);
        return ResponseDTO.getSuccessResult("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
    }
    private void increaseIssuedCouponQuantity(long couponId) {
        Coupon existingCoupon = findCouponPessimisticLock(couponId);
        Coupon updatecoupon = existingCoupon.increaseIssuedQuantity(existingCoupon);
        couponRepository.increaseIssuedQuantity(updatecoupon);
    }
    private Coupon findCouponPessimisticLock(long couponId) {
        return couponRepository.findCouponByIdPessimisticLock(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }
}
