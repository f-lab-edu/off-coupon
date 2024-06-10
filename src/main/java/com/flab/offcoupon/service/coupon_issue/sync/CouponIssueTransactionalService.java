package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CouponIssueCheckVo;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.DUPLICATED_COUPON;

@Service
@RequiredArgsConstructor
public class CouponIssueTransactionalService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final ObjectProvider<CouponIssueTransactionalService> proxy;

    @Transactional
    public void increaseIssuedCouponQuantity(long couponId) {
        // 쿠폰 조회
        Coupon existingCoupon = couponRepository.getCouponById(couponId);
        // 조회된 쿠폰의 반정규화 칼럼인 issuedQuantity 증가
        Coupon updatecoupon = existingCoupon.increaseIssuedQuantity(existingCoupon);
        // 데이터 베이스에 반영
        couponRepository.increaseIssuedQuantity(updatecoupon);
    }
    @Transactional
    public void saveCouponIssue(long memberId, long couponId, LocalDateTime currentDateTime) {
        proxy.getObject().checkAlreadyIssueHistory(memberId, couponId, currentDateTime);
        CouponIssue couponIssue = CouponIssue.create(memberId, couponId, true);
        couponIssueRepository.save(couponIssue);
    }

    private void checkAlreadyIssueHistory(long memberId, long couponId, LocalDateTime currentDateTime) {
        LocalDate currentDate = currentDateTime.toLocalDate();
        if (couponIssueRepository.existCouponIssue(new CouponIssueCheckVo(memberId, couponId, currentDate))) {
            throw new DuplicatedCouponException(DUPLICATED_COUPON.formatted(memberId, couponId));
        }
    }
}
