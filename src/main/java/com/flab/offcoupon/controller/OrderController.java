package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.response.AvailableCouponsByMemberIdResponse;
import com.flab.offcoupon.service.coupon_use.OrderService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    /**
     * 사용 가능한 쿠폰 목록 조회
     *
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @return 사용 가능한 쿠폰 목록
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/available-coupons")
    public ResponseEntity<ResponseDTO<List<AvailableCouponsByMemberIdResponse>>> getAvailableCoupons(@RequestParam final long memberId,
                                                                                                     @RequestParam final long productId) {
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAvailableCoupons(memberId, productId, now));
    }
}
