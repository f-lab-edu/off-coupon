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
     * 상품 주문 시 사용 가능한 쿠폰 조회<br>
     * <ul>
     *     <li>사용 가능한 쿠폰 목록 조회 조건</li>
     *     <ol>
     *         <li>product_id와 member_id가 가 매개변수로 받은 ID식별자인 경우</li>
     *         <li>쿠폰의 상태가 활성화인 상태이고, 현재 날짜 기준으로 유효 기간 범위 내에 있는 경우/li>
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

    /**
     * 쿠폰 상태를 사용 완료로 변경
     * @param couponIssueIds 쿠폰 발급 ID 목록
     */
    void useCoupon(List<Long> couponIssueIds);
}
