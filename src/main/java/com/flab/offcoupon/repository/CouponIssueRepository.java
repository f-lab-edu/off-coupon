package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.CouponIssue;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface CouponIssueRepository {
    void save(CouponIssue couponIssue);

    boolean existCouponIssueByMemberIdAndCouponId(long memberId, long couponId, LocalDateTime currentDateTime);
}
