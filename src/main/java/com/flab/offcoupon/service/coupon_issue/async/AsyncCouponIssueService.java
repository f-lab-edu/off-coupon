package com.flab.offcoupon.service.coupon_issue.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.component.DistributeLockExecutorWithRedisson;
import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.dto.request.CouponIssueRequestForQueue;
import com.flab.offcoupon.exception.coupon.CouponIssueException;
import com.flab.offcoupon.repository.redis.RedisRepository;
import com.flab.offcoupon.service.cache.CouponCacheService;
import com.flab.offcoupon.service.cache.EventCacheService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestQueueKey;
import static com.flab.offcoupon.util.LockMagicNumber.LOCK_LEASE_MILLI_SECOND;
import static com.flab.offcoupon.util.LockMagicNumber.LOCK_WAIT_MILLI_SECOND;

/**
 * 비동기적으로 쿠폰을 발급하는 서비스 클래스입니다.
 * 이 클래스는 Redis를 사용하여 쿠폰 발급 작업을 비동기적으로 처리합니다.
 */
@RequiredArgsConstructor
@Service
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final DistributeLockExecutorWithRedisson distributeLockExecutorWithRedisson;
    private final EventCacheService eventCacheService;
    private final CouponCacheService couponCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 쿠폰 발급을 수행하는 메서드입니다.
     *
     * @param currentDateTime 현재 시각
     * @param eventId          이벤트 ID
     * @param couponId         쿠폰 ID
     * @param memberId         회원 ID
     * @return                 응답 DTO
     */
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) {
        // 이벤트 캐시에서 발급 가능 날짜 및 시간 검증
        checkIssuableEventPeriodAndTime(currentDateTime, eventId);
        // 쿠폰 캐시 가져오기
        CouponRedisEntity coupon = couponCacheService.getCoupon(couponId);
        // 레디스 명령 자체는 싱글 스레드에서 처리되기 때문에 본래 동시성 이슈는 발생하지 않습니다.
        // 그러나 레디스를 호출하는 부분이 여러 곳으로 분리되어 있다면 동시성 문제가 발생할 수 있습니다.
        // 따라서 Redisson의 분산 락을 활용하여 동시성 문제를 안전하게 해결합니다.
        distributeLockExecutorWithRedisson.execute("redisson_lock" + couponId, LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, () -> {
            couponIssueRedisService.checkCouponIssueQuantityAndDuplicate(coupon, memberId);
            issueRequest(couponId, memberId);
        });
        return ResponseDTO.getSuccessResult("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
    }

    // 이벤트 캐시에서 이벤트 정보를 가져와서 이벤트 기간 및 시간을 검증합니다.
    private void checkIssuableEventPeriodAndTime(LocalDateTime currentDateTime, long eventId) {
        EventRedisEntity event = eventCacheService.getEvent(eventId);
        event.availableIssuePeriodAndTime(currentDateTime);
    }

    /**
     * 검증이 완료된 이후, 쿠폰 발급 요청을 처리하는 메서드 입니다.
     * 1. SET에 쿠폰 발급 요청 추가 : 중복 발급 요청을 방지 및 총 요청 개수 카운팅을 하기 위해 사용됩니다
     * 2. LIST에 쿠폰 발급 요청 적재 : 선착 순 대기 큐 목록으로서 사용됩니다
     *
     * @param couponId 쿠폰 ID
     * @param memberId 회원 ID
     */
    private void issueRequest(long couponId, long memberId) {
        CouponIssueRequestForQueue issueRequestForQueue = new CouponIssueRequestForQueue(couponId, memberId);
        try {
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(memberId));
            // 쿠폰 발급 큐에 적재
            redisRepository.rPush(getIssueRequestQueueKey(), objectMapper.writeValueAsString(issueRequestForQueue));
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(FAIL_COUPON_ISSUE_REQUEST.formatted(issueRequestForQueue));
        }
    }
}
