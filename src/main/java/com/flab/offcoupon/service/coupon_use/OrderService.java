package com.flab.offcoupon.service.coupon_use;

import com.flab.offcoupon.domain.entity.Product;
import com.flab.offcoupon.domain.vo.persistence.order.CouponIssuesAreActiveVo;
import com.flab.offcoupon.domain.vo.persistence.order.MemberIdProductIdNowVo;
import com.flab.offcoupon.domain.vo.persistence.order.ValidateNowIsBetweenPeriodVo;
import com.flab.offcoupon.dto.request.OrderProductRequest;
import com.flab.offcoupon.dto.response.AvailableCouponsByMemberIdResponse;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.ProductRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final CouponIssueRepository couponIssueRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    @Transactional(readOnly = true)
    public ResponseDTO<List<AvailableCouponsByMemberIdResponse>> getAvailableCoupons(final long memberId, final  long productId, final LocalDateTime now) {
        return ResponseDTO.getSuccessResult(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now))
                .stream()
                .map(AvailableCouponsByMemberIdResponse::new)
                .collect(Collectors.toList()));
    }
    @Transactional
    public void orderProduct(final long productId, final OrderProductRequest request, LocalDateTime now) {
        validateCouponIsAvailable(request, now);
        // 3. 주문 - 쿠폰 사용 처리(상태변경), 주문 정보 저장, 주문에 사용된 쿠폰 저장
        Product product = productRepository.getProductById(productId);
        System.out.println("product : " + product);

    }

    /**
     * 쿠폰 사용 가능 여부 검증
     * @param request 주문 요청 정보
     * @param now 현재 시간
     */
    private void validateCouponIsAvailable(OrderProductRequest request, LocalDateTime now) {
        // 1. 쿠폰들의 상태가 ACTIVE인지 확인
        List<CouponIssuesAreActiveVo> couponIssueStatus = couponIssueRepository.validateStatusIsActive(request.getCouponIssueId());
        System.out.println("couponIssueStatus : " + couponIssueStatus);
        for (CouponIssuesAreActiveVo couponIssue : couponIssueStatus) {
            if (!couponIssue.isActive()) {
                throw new IllegalArgumentException("쿠폰의 상태가 ACTIVE가 아닙니다. couponIssueId: " + couponIssue.couponIssueId());
            }
        }

        // 2. 현재 시간이 쿠폰의 유효기간 범위내에 있는지 확인
        List<ValidateNowIsBetweenPeriodVo> isBetweenValidatePeriodVo = couponRepository.validateNowIsBetweenPeriod(request.getCouponId(), now);
        for (ValidateNowIsBetweenPeriodVo isBetweenValidatePeriod : isBetweenValidatePeriodVo) {
            if (!isBetweenValidatePeriod.isBetweenValidatePeriod()) {
                throw new IllegalArgumentException("쿠폰의 유효기간이 아닙니다. couponId: %s, validateStartDate: %s, validateEndDate : %s "
                        .formatted(isBetweenValidatePeriod.couponId(), isBetweenValidatePeriod.validateStartDate(), isBetweenValidatePeriod.validateEndDate()));
            }
        }
    }
}
