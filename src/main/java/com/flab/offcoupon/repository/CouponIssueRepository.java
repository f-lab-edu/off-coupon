package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.couponissue.CouponIssueCheckVo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CouponIssueRepository {
    void save(CouponIssue couponIssue);
    boolean existCouponIssue(CouponIssueCheckVo couponIssueCheckVo);
}
