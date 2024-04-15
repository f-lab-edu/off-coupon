package com.flab.offcoupon.service.coupon_use;

import com.flab.offcoupon.domain.entity.OrderDetail;
import com.flab.offcoupon.domain.entity.helper.AppliedCouponInfo;
import com.flab.offcoupon.domain.entity.helper.AvailableCouponInfo;
import com.flab.offcoupon.domain.entity.helper.OrderInfo;
import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponIssuesAreActiveVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponValidationPeriodVo;
import com.flab.offcoupon.domain.vo.persistence.order.MemberIdProductIdNowVo;
import com.flab.offcoupon.dto.request.OrderProductRequest;
import com.flab.offcoupon.dto.response.AvailableCouponsByMemberIdResponse;
import com.flab.offcoupon.exception.coupon.CouponStatusException;
import com.flab.offcoupon.exception.coupon.CouponUsageInvalidPeriodException;
import com.flab.offcoupon.repository.mysql.*;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.flab.offcoupon.domain.entity.OrderCoupon.createOrderCoupon;
import static com.flab.offcoupon.domain.entity.OrderDetail.createOrderDetail;
import static com.flab.offcoupon.domain.entity.helper.OrderInfo.createOrderInfo;
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
     * <p>
     * Query에서 로직 사용을 최소화하는것이 목표입니다.<br>
     * Query는 디버깅이 어려우며 수정이 필요할때 유연하게 대응하기 어렵고, 테스트 코드를 작성하기도 어렵습니다.<br>
     * 따라서 데이터 조회 쿼리를 심플하게 만들고 모든 가공은 애플리케이션 내에서 하도록 진행했습니다.<br>
     * </p>
     * <p>
     * 아래는 쿼리에서 로직 사용을 피하기 위한 경우입니다.
     * <ol>
     *     <li>query에서 분기를 태우는 case-when-then</li>
     *     <li>query에서 값을 계산하는 경우</li>
     *     <li>query에서 비즈니스 로직이 있는 경우</li>
     * </ol>
     * </p>
     *
     * @param memberId  회원 ID
     * @param productId 상품 ID
     * @param now       현재 날짜
     * @return 사용 가능한 쿠폰 목록
     */
    @Transactional(readOnly = true)
    public ResponseDTO<List<AvailableCouponsByMemberIdResponse>> getAvailableCoupons(final long memberId, final long productId, final LocalDateTime now) {
        List<AvailableCouponsByMemberIdVo> availableCouponData =
                couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now));

        // 할인가격 기준으로 내림차순
        List<AvailableCouponInfo> availableCouponInfos = availableCouponData.stream()
                .map(AvailableCouponInfo::new)
                .sorted(Comparator.comparing(AvailableCouponInfo::getAppliedDiscountPrice).reversed())
                .toList();

        return ResponseDTO.getSuccessResult(filterAvailableCoupons(availableCouponInfos));
    }

    /**
     * 사용 가능한 쿠폰 목록을 필터링합니다.
     * 필터링 조건은 아래와 같습니다.
     * <ol>
     *     <li>내림차순으로 정렬된 할인 가격을 하나씩 꺼내어 누적합니다.</li>
     *     <li>누적된 할인가격을 반영한 가격이 최소 주문 금액 이상이라면 리스트에 추가합니다.</li>
     *     <li>현재 할인 가격이 2번 조건에서 일치하지 않을 경우 누적 할인 가격에서 제외합니다.</li>
     * </ol>
     * @param availableCouponInfos
     * @return
     */
    private List<AvailableCouponsByMemberIdResponse> filterAvailableCoupons(List<AvailableCouponInfo> availableCouponInfos) {
        AtomicReference<BigDecimal> accumulatedDiscountPrice = new AtomicReference<>(BigDecimal.ZERO);
        List<AvailableCouponsByMemberIdResponse> responseList = new ArrayList<>();

        availableCouponInfos.forEach(info -> {
            BigDecimal discountPrice = info.getAppliedDiscountPrice();
            accumulatedDiscountPrice.updateAndGet(price -> price.add(discountPrice));
            if (isOverThanMinOrderPrice(info.getProductPrice(), info.getMinProductPrice(), accumulatedDiscountPrice.get())) {
                responseList.add(new AvailableCouponsByMemberIdResponse(info));
            } else {
                accumulatedDiscountPrice.updateAndGet(price -> price.subtract(discountPrice));
            }
        });

        return responseList;
    }


    /**
     * 최소 주문 금액과 비교하여 조건에 맞는 쿠폰인지 확인합니다.
     *
     * @param price                    상품 가격
     * @param minOrderPrice            최소 주문 금액
     * @param accumulatedDiscountPrice 누적된 할인 가격
     * @return 할인이 적용된 금액이 최소 주문 금액보다 크거나 같으면 true, 아니면 false
     */
    private boolean isOverThanMinOrderPrice(BigDecimal price, BigDecimal minOrderPrice, BigDecimal accumulatedDiscountPrice) {
        // 쿠폰 할인을 적용한 가격을 구함
        BigDecimal appliedDiscountPriceToProduct = price.subtract(accumulatedDiscountPrice);
        return appliedDiscountPriceToProduct.compareTo(minOrderPrice) >= 0;
    }

    /**
     * 상품 주문 및 쿠폰 사용 처리
     *
     * @param productId 상품 ID
     * @param request   주문 요청 정보
     * @param now       현재 시간
     */
    @Transactional
    public ResponseDTO<String> orderProduct(final long productId, final OrderProductRequest request, LocalDateTime
            now) {
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
        couponIssueRepository.useCoupon(request.getCouponIssueId());
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
        if (couponIssueStatus.stream().anyMatch(couponIssue -> !couponIssue.isActive())) {
            throw new CouponStatusException(COUPON_IS_NOT_ACTIVE.formatted(couponIssueStatus.get(0).couponIssueId()));
        }

        // 2. 현재 시간이 쿠폰의 유효기간 범위내에 있는지 확인
        List<CouponValidationPeriodVo> isBetweenValidatePeriodVo = couponRepository.getCouponValidationPeriod(request.getCouponId());
        for (CouponValidationPeriodVo validationPeriod : isBetweenValidatePeriodVo) {
            if (!isNowBetweenValidatePeriod(now, validationPeriod)) {
                throw new CouponUsageInvalidPeriodException(COUPON_USAGE_INVALID_PERIOD
                        .formatted(validationPeriod.couponId(), validationPeriod.validateStartDate(), validationPeriod.validateEndDate()));
            }
        }
    }
    private boolean isNowBetweenValidatePeriod(LocalDateTime now, CouponValidationPeriodVo validationPeriod) {
        if(validationPeriod.validateStartDate() == null || validationPeriod.validateEndDate() == null) {
            return false;
        }
        /**
         * 현재 날짜가 유효 기간 범위 내에 있는지 확인
         * Duration.between(a,b) : a와 b 사이의 시간을 반환, a가 b보다 시간상으로 이전이면 양수, 이후면 음수
         */
        Duration startDuration = Duration.between(validationPeriod.validateStartDate(), now);
        Duration endDuration = Duration.between(now, validationPeriod.validateEndDate());

        return (startDuration.isZero() || !startDuration.isNegative()) &&
                (endDuration.isZero() || !endDuration.isNegative());
    }
}
