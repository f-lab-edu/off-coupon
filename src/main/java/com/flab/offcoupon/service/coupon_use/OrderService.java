package com.flab.offcoupon.service.coupon_use;

import com.flab.offcoupon.domain.vo.persistence.order.MemberIdProductIdNowVo;
import com.flab.offcoupon.dto.response.AvailableCouponsByMemberIdResponse;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final CouponIssueRepository couponIssueRepository;
    @Transactional(readOnly = true)
    public ResponseDTO<List<AvailableCouponsByMemberIdResponse>> getAvailableCoupons(final long memberId, final  long productId, final LocalDateTime now) {
        return ResponseDTO.getSuccessResult(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now))
                .stream()
                .map(AvailableCouponsByMemberIdResponse::new)
                .collect(Collectors.toList()));
    }
}
