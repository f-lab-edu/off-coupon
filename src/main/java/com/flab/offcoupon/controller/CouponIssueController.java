package com.flab.offcoupon.controller;

import com.flab.offcoupon.service.coupon_issue.CouponIssueRequestService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 쿠폰 발급 요청을 처리하는 컨트롤러입니다.
 *
 * <p>쿠폰 발급 요청은 동기식과 비동기식으로 처리할 수 있습니다.</p>
 * <ol>
 *   <li>동기식(syncIssue) : 쿠폰 발급 요청을 받은 후, 즉시 MySQL에 반영해서 쿠폰 발급 결과를 반환합니다.</li>
 *   <li>비동기식(asyncIssue)</li>
 *      <p>a. 사용자가 쿠폰 발급 요청한다</p>
 *      <p>b. 서버에서 해당 요청을 받아 Redis로 캐싱된 데이터와 Set 자료구조를 통해 검증한다</p>
 *      <p>c. 검증이 완료된 요청은 RabbitMQ를 활용해 대기큐에 적재한다</p>
 *      <p>d. 스케줄링을 통해 Queue에 저장된 메세지를 하나씩 꺼내어 쿠폰 발급 히스토리 INSERT, 총 쿠폰 발급 수량 UPDATE</p>
 *      <p>e. 메세지를 하나씩 꺼내고 이력을 저장할때마다 해당 유저에게 SSE 알람 전송</p>
 *     <p> 정리 : 유저 트래픽과 쿠폰 발급 트랜잭션 분리 -> 목표 : Redis를 통한 트래픽 대응 및 MySQL 트래픽 제어</p>
 * </ol>
 */
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/issues-sync")
    public ResponseEntity<ResponseDTO<String>> syncIssue(@PathVariable final long eventId,
                                                 @RequestParam final long couponId,
                                                 @RequestParam final long memberId) throws InterruptedException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        //SecurityContextHolder.getContext().getAuthentication() // anonymous
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueRequestService.syncIssueCoupon(currentDateTime, eventId, couponId, memberId));
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/issues-async")
    public ResponseEntity<ResponseDTO<String>> asyncIssue(@PathVariable final long eventId,
                                                  @RequestParam final long couponId,
                                                  @RequestParam final long memberId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return ResponseEntity.status(HttpStatus.CREATED).body(couponIssueRequestService.asyncIssueCoupon(currentDateTime, eventId, couponId, memberId));
    }
}
