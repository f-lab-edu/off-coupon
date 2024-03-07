package com.flab.offcoupon.controller;

import com.flab.offcoupon.service.couponIssue.CouponIssueRequestService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 쿠폰 발급 요청을 처리하는 컨트롤러입니다.
 *
 * 쿠폰 발급 요청은 동기식과 비동기식으로 처리할 수 있습니다.
 * 1. 동기식 : 쿠폰 발급 요청을 받은 후, 즉시 MuSQL에 반영해서 쿠폰 발급 결과를 반환합니다.
 * 2. 비동기식 : 쿠폰 발급 요청을 받은 후, Redis에 해당 요청을 저장한 뒤 별도의 쿠폰 발급 서버에서 스케줄링으로 쿠폰 발급 대상에 대한 MySQL트랜잭션을 처리합니다.
 *  (유저 트래픽과 쿠폰 발급 트랜잭션 분리) - Redis를 통한 트래픽 대응 및 MySQL 트래픽 제어
 */
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/issues-sync")
    public ResponseEntity<ResponseDTO> syncIssue(@PathVariable final long eventId,
                                                 @RequestParam final long couponId,
                                                 @RequestParam final long memberId) throws InterruptedException {
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueRequestService.syncIssueCoupon(currentDateTime, eventId, couponId, memberId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/issues-async")
    public ResponseEntity<ResponseDTO> asyncIssue(@PathVariable final long eventId,
                                                  @RequestParam final long couponId,
                                                  @RequestParam final long memberId) throws InterruptedException {
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueRequestService.asyncIssueCoupon(currentDateTime, eventId, couponId, memberId));
    }
}
