package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.request.OrderProductRequest;
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
     * @param memberId  회원 ID
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

    /**
     * 상품 주문<br>
     * 결제 SDK 연동은 현재 진행 중인 프로젝트에서 메인으로 다루지 않기 때문에 제외했습니다.
     *
     * @param productId 상품 ID
     * @param orderProductRequest 주문 요청 정보
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/products/{productId}")
    public ResponseEntity<ResponseDTO<String>> orderProduct(@PathVariable final long productId,
                                          @RequestBody final OrderProductRequest orderProductRequest) {
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.getSuccessResult(orderService.orderProduct(productId,orderProductRequest, now)));
    }
}
