package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import com.flab.offcoupon.domain.vo.persistence.order.ValidateNowIsBetweenPeriodVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CouponRepository {
    void save(Coupon coupon);

    Optional<Coupon> findCouponById(long couponId);

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
     * 현재 시간이 쿠폰의 유효기간 범위내에 있는지 확인
     *
     * @param couponIds        쿠폰 ID 리스트
     * @param currentDateTime  현재 시간
     * @return 현재 시간이 쿠폰의 유효기간 범위내에 있는지 여부
     */
    List<ValidateNowIsBetweenPeriodVo> validateNowIsBetweenPeriod(@Param("couponIds") List<Long> couponIds,
                                                                  @Param("currentDateTime") LocalDateTime currentDateTime);
}
