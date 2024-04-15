package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponValidationPeriodVo;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;

@Mapper
public interface CouponRepository {
    void save(Coupon coupon);

    /**
     * 쿠폰 ID로 쿠폰을 조회합니다.<br>
     * Coupon 객체를 찾을 수도 있고, 없을 수도 있습니다.
     * Optional을 return하여 선택권을 제공합니다.
     *
     * @param couponId
     * @return Optinal한 쿠폰 객체
     */
    Optional<Coupon> findCouponById(long couponId);

    /**
     * 쿠폰 ID로 쿠폰을 조회해서 Coupon객체를 반환합니다.<br>
     *
     * @param couponId 쿠폰 ID
     * @return 쿠폰 객체
     */
    default Coupon getCouponById(long couponId) {
        return findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }

    /**
     * 쿠폰 ID로 쿠폰 리스트를을조회합니다.<br>
     *
     * @param couponIds 쿠폰 ID 리스트
     * @return List<Coupon> 쿠폰 객체 리스트
     */
    List<Coupon> findCouponsByIds(List<Long> couponIds);


    Optional<Coupon> findCouponByIdPessimisticLock(long couponId);

    void increaseIssuedQuantity(Coupon coupon);

    /**
     * 쿠폰 총 발급 수량 업데이트<br>
     * 쿠폰 발급 비동기 API에서 사용되는 메소드로, 반정규화된 issued_quantity를 업데이트한다.
     *
     * @param updateTotalIssuedQuantityVo
     */
    void updateTotalIssuedCouponQuantity(UpdateTotalIssuedQuantityVo updateTotalIssuedQuantityVo);

    /**
     * 쿠폰의 유효시간 범위 조회
     *
     * @param couponIds       쿠폰 ID 리스트
     * @return 현재 시간이 쿠폰의 유효기간 범위내에 있는지 여부
     */
    List<CouponValidationPeriodVo> getCouponValidationPeriod(@Param("couponIds") List<Long> couponIds);
}
