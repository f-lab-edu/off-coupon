package com.flab.offcoupon.service.coupon_use;

import com.flab.offcoupon.domain.entity.OrderDetail;
import com.flab.offcoupon.domain.entity.params.AppliedCouponInfo;
import com.flab.offcoupon.domain.entity.params.OrderInfo;
import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponIssuesAreActiveVo;
import com.flab.offcoupon.domain.vo.persistence.order.MemberIdProductIdNowVo;
import com.flab.offcoupon.domain.vo.persistence.order.ValidateNowIsBetweenPeriodVo;
import com.flab.offcoupon.dto.request.OrderProductRequest;
import com.flab.offcoupon.dto.response.AvailableCouponsByMemberIdResponse;
import com.flab.offcoupon.exception.coupon.CouponStatusException;
import com.flab.offcoupon.exception.coupon.CouponUsageInvalidPeriodException;
import com.flab.offcoupon.repository.mysql.*;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.flab.offcoupon.domain.entity.OrderCoupon.createOrderCoupon;
import static com.flab.offcoupon.domain.entity.OrderDetail.createOrderDetail;
import static com.flab.offcoupon.domain.entity.params.OrderInfo.createOrderInfo;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_IS_NOT_ACTIVE;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_USAGE_INVALID_PERIOD;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final CouponIssueRepository couponIssueRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderCouponRepository orderCouponRepository;

    /**
     * 사용 가능한 쿠폰 목록 조회
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @param now 현재 날짜
     * @return 사용 가능한 쿠폰 목록
     */
    @Transactional(readOnly = true)
    public ResponseDTO<List<AvailableCouponsByMemberIdResponse>> getAvailableCoupons(final long memberId, final long productId, final LocalDateTime now) {
        List<AvailableCouponsByMemberIdVo> availableCoupons =
                couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now));
        List<AvailableCouponsByMemberIdResponse> responseList = new ArrayList<>();
        filterAvailableCouponsWithMinPrice(productId, availableCoupons, responseList);
        return ResponseDTO.getSuccessResult(responseList);
    }

    /**
     * 최소 주문 금액과 비교하여 조건에 맞는 쿠폰만 결과 리스트에 추가합니다.
     *
     * @param productId        상품 ID
     * @param availableCoupons 사용 가능한 쿠폰 목록
     * @param responseList     결과 리스트
     */
    private void filterAvailableCouponsWithMinPrice(long productId, List<AvailableCouponsByMemberIdVo> availableCoupons, List<AvailableCouponsByMemberIdResponse> responseList) {
        // min_price(상품의 최소 주문 금액)과 비교하여 조건에 맞는 쿠폰만 결과 리스트에 추가합니다.
        if (!availableCoupons.isEmpty()) {
            long totalOrderPrice = 0;
            for (AvailableCouponsByMemberIdVo availableCoupon : availableCoupons) {
                // 최소 주문 금액까지 할인 가능한 금액 누적
                totalOrderPrice += availableCoupon.discountedPrice();
                // 할인 금액이 min_price보다 작거나 같으면 결과 리스트에 추가
                if (totalOrderPrice <= productRepository.getProductMinOrderPriceById(productId)) {
                    AvailableCouponsByMemberIdResponse response = new AvailableCouponsByMemberIdResponse(availableCoupon);
                    responseList.add(response);
                }
            }
        }
    }

    /**
     * 상품 주문 및 쿠폰 사용 처리
     *
     * @param productId 상품 ID
     * @param request   주문 요청 정보
     * @param now       현재 시간
     */
    @Transactional
    public ResponseDTO<String> orderProduct(final long productId, final OrderProductRequest request, LocalDateTime now) {
        validateCouponIsAvailable(request, now);
        // 3. 주문 정보 저장
        OrderInfo orderInfo = createOrderInfo(
                productRepository.getProductById(productId),
                request.getQuantity(),
                couponRepository.findCouponsByIds(request.getCouponId()));
        OrderDetail orderDetail = createOrderDetail(orderInfo);
        orderDetailRepository.save(orderDetail);
        long orderDetailId = orderDetail.getId();

        // 4. 주문에 사용된 쿠폰 저장
        for (AppliedCouponInfo couponInfo : orderInfo.getAppliedCouponInfos()) {
            orderCouponRepository.save(createOrderCoupon(orderDetailId, couponInfo));
        }

        // 5. 쿠폰 사용 처리
        couponIssueRepository.updateCouponStatus(request.getCouponIssueId());
        return ResponseDTO.getSuccessResult("쿠폰 처리 및 주문이 완료되었습니다.");
    }

    /**
     * 쿠폰 사용 가능 여부 검증
     *
     * @param request 주문 요청 정보
     * @param now     현재 시간
     */
    private void validateCouponIsAvailable(OrderProductRequest request, LocalDateTime now) {
        // 1. 쿠폰들의 상태가 ACTIVE인지 확인
        List<CouponIssuesAreActiveVo> couponIssueStatus = couponIssueRepository.validateStatusIsActive(request.getCouponIssueId());
        for (CouponIssuesAreActiveVo couponIssue : couponIssueStatus) {
            if (!couponIssue.isActive()) {
                throw new CouponStatusException(COUPON_IS_NOT_ACTIVE.formatted(couponIssue.couponIssueId()));
            }
        }

        // 2. 현재 시간이 쿠폰의 유효기간 범위내에 있는지 확인
        List<ValidateNowIsBetweenPeriodVo> isBetweenValidatePeriodVo = couponRepository.validateNowIsBetweenPeriod(request.getCouponId(), now);
        for (ValidateNowIsBetweenPeriodVo isBetweenValidatePeriod : isBetweenValidatePeriodVo) {
            if (!isBetweenValidatePeriod.isBetweenValidatePeriod()) {
                throw new CouponUsageInvalidPeriodException(COUPON_USAGE_INVALID_PERIOD
                        .formatted(isBetweenValidatePeriod.couponId(), isBetweenValidatePeriod.validateStartDate(), isBetweenValidatePeriod.validateEndDate()));
            }
        }
    }
}
