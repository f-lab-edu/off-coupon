package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CountByCouponIdVo;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CouponIssueCheckVo;
import com.flab.offcoupon.domain.vo.persistence.mypage.AllCouponsByMemberIdVo;
import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponIssuesAreActiveVo;
import com.flab.offcoupon.domain.vo.persistence.order.MemberIdProductIdNowVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;


@Mapper
public interface CouponIssueRepository {
    void save(CouponIssue couponIssue);

    boolean existCouponIssue(CouponIssueCheckVo couponIssueCheckVo);

    List<CountByCouponIdVo> countCouponIdForToday();

    List<Long> couponIssueIdListForToday();

    Optional<CouponIssue> findCouponIssueById(long id);

    void updateCheckFlag(CouponIssue couponIssue);

    /**
     * 마이페이지에서 본인이 가진 모든 쿠폰 조회
     *
     * @param memberId 회원 ID
     * @return 쿠폰 목록
     */

    List<AllCouponsByMemberIdVo> getAllCoupons(long memberId);

    /**
     * 물건 구매 시 사용 가능한 쿠폰 조회<br>
     * <ul>
     *     <li>사용 가능한 쿠폰 목록 조회 조건</li>
     *     <ol>
     *         <li>할인율이 적용된 가격(discounted_price) 기준으로 내림차순 정렬</li>
     *         <li>discounted_price는 쿠폰의 할인율 또는 할인액에 따라 원래 상품 가격 혹은 할인 상품 가격에 계산</li>
     *         <li>쿠폰의 상태가 활성화인 상태이고, 현재 날짜 기준으로 유효 기간 범위 내에 있는 쿠폰 조회</li>
     *     </ol>
     * </ul>
     *
     * @param memberIdProductIdNowVo 회원 ID, 상품 ID, 현재 날짜
     * @return 사용 가능한 쿠폰 목록
     */
    List<AvailableCouponsByMemberIdVo> getAvailableCoupons(final MemberIdProductIdNowVo memberIdProductIdNowVo);

    /**
     * 발급된 쿠폰들의 상태가 활성화인지 확인
     *
     * @param couponIssueIds 쿠폰 발급 ID 목록
     * @return 쿠폰의 상태가 활성화인지 여부
     */

    List<CouponIssuesAreActiveVo> validateStatusIsActive(List<Long> couponIssueIds);
}
