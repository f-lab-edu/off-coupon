package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CountByCouponIdVo;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CouponIssueCheckVo;
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
}
