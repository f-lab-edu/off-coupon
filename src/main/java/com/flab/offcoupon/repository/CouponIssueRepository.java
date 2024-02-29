package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.CouponIssue;
import com.flab.offcoupon.domain.persistence.couponissue.CouponIssueCheckVo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CouponIssueRepository {
    void save(CouponIssue couponIssue);
    boolean existCouponIssue(CouponIssueCheckVo couponIssueCheckVo);
}
