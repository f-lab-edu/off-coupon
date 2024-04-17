package com.flab.offcoupon.service.mypage;

import com.flab.offcoupon.dto.response.AllCouponsByMemberIdResponse;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final CouponIssueRepository couponIssueRepository;

    public ResponseDTO<List<AllCouponsByMemberIdResponse>> getAllCoupons(long memberId) {
        return ResponseDTO.getSuccessResult(couponIssueRepository.getAllCoupons(memberId)
                .stream()
                .map(AllCouponsByMemberIdResponse::new)
                .toList());
    }
}
