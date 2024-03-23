package com.flab.offcoupon.service.coupon_issue.async;

import com.flab.offcoupon.component.rabbitmq.Producer;
import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.repository.redis.RedisRepository;
import com.flab.offcoupon.service.cache.CouponCacheService;
import com.flab.offcoupon.service.cache.EventCacheService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.flab.offcoupon.util.CouponRabbitMQUtils.EXCHANGE_NAME;
import static com.flab.offcoupon.util.CouponRabbitMQUtils.ROUTING_KEY;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;

/**
 * 비동기적으로 쿠폰을 발급하는 서비스 클래스입니다.
 * 이 클래스는 Redis와 RabbitMQ를 사용하여 쿠폰 발급 작업을 비동기적으로 처리합니다.
 */
@RequiredArgsConstructor
@Service
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final EventCacheService eventCacheService;
    private final CouponCacheService couponCacheService;
    private final Producer producer;
    /**
     * 비동기로 쿠폰 발급을 수행하는 메서드입니다.
     *
     * @param currentDateTime 현재 시각
     * @param eventId          이벤트 ID
     * @param couponId         쿠폰 ID
     * @param memberId         회원 ID
     * @return 응답 DTO
     */
    public ResponseDTO<String> issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) {
        checkIssuableEventPeriodAndTime(currentDateTime, eventId);
        CouponRedisEntity coupon = couponCacheService.getCoupon(couponId);
        couponIssueRedisService.checkCouponIssueQuantityAndDuplicate(coupon, memberId);
        issueRequest(couponId, memberId);
        return ResponseDTO.getSuccessResult("쿠폰이 발급 요청되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
    }

    /**
     * 이벤트 캐시에서 이벤트 정보를 가져와서 이벤트 기간 및 시간을 검증합니다.
     */
    private void checkIssuableEventPeriodAndTime(LocalDateTime currentDateTime, long eventId) {
        EventRedisEntity event = eventCacheService.getEvent(eventId);
        event.availableIssuePeriodAndTime(currentDateTime);
    }

    /**
     * 검증이 완료된 이후, 쿠폰 발급 요청을 처리하는 메서드 입니다.<br>
     * <ol>
     *     <li> Redis의 SET 자료구조에 쿠폰 발급 요청 추가 : 중복 발급 요청을 방지 및 총 요청 개수 카운팅을 하기 위해 사용됩니다</li>
     *     <li> RabbitMQ에 쿠폰 발급 요청 적재 : 선착 순 대기 큐 목록으로서 사용됩니다.</li>
     * </ol>
     *
     * @param couponId 쿠폰 ID
     * @param memberId 회원 ID
     */
    private void issueRequest(long couponId, long memberId) {
        redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(memberId));
        producer.producer(EXCHANGE_NAME, ROUTING_KEY, new CouponIssueMessageForQueue(couponId, memberId));
    }
}
