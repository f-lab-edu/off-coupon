package com.flab.offcoupon.service.coupon_issue.async;

import static com.flab.offcoupon.util.CouponRabbitMQConstants.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.flab.offcoupon.component.rabbitmq.Producer;
import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.domain.redis.IssueRequestKey;
import com.flab.offcoupon.dto.request.IssueRequestParameter;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.model.PositiveLong;
import com.flab.offcoupon.service.cache.CouponCacheService;
import com.flab.offcoupon.service.cache.EventCacheService;
import com.flab.offcoupon.service.coupon_issue.async.dto.EventAndCouponCache;
import com.flab.offcoupon.util.ResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * 비동기적으로 쿠폰을 발급하는 서비스 클래스입니다.
 * 이 클래스는 Redis와 RabbitMQ를 사용하여 쿠폰 발급 작업을 비동기적으로 처리합니다.
 */
@RequiredArgsConstructor
@Service
public class AsyncCouponIssueService {

	private final CouponIssueRedisService couponIssueRedisService;
	private final EventCacheService eventCacheService;
	private final CouponCacheService couponCacheService;
	private final Producer producer;

	/**
	 * 비동기로 쿠폰 발급을 수행하는 메서드입니다.
	 *
	 * @param currentDateTime 현재 시각
	 * @Param requestParameter 쿠폰 발급 요청 파라미터
	 * @return 응답 DTO
	 */
	public ResponseDTO<String> issueCoupon(final LocalDateTime currentDateTime,
		final IssueRequestParameter requestParameter) {
		EventAndCouponCache eventAndCouponCache = getEventAndCouponCache(requestParameter);
		checkIssuableEventPeriodAndTime(currentDateTime, eventAndCouponCache.eventCache());
		couponIssueRedisService.checkCouponIssueQuantityAndDuplicate(eventAndCouponCache,
			requestParameter.getMemberId());
		issueRequest(new IssueRequestKey(new PositiveLong(requestParameter.getCouponId()),
			new PositiveLong(requestParameter.getMemberId())));
		return ResponseDTO.getSuccessResult(
			"쿠폰이 발급 요청되었습니다. memberId : %s, couponId : %s".formatted(requestParameter.getMemberId(),
				requestParameter.getCouponId()));
	}

	/**
	 * 캐싱된 데이터 중 이벤트와 쿠폰 정보를 가져와서 EventAndCouponCache 객체를 생성합니다.
	 * @param requestParameter 쿠폰 발급 요청 파라미터
	 * @return 이벤트와 쿠폰 정보를 담은 EventAndCouponCache 객체
	 **/
	private EventAndCouponCache getEventAndCouponCache(final IssueRequestParameter requestParameter) {
		EventRedisEntity event = eventCacheService.getEvent(requestParameter.getEventId());
		CouponRedisEntity coupon = couponCacheService.getCoupon(requestParameter.getCouponId());
		return new EventAndCouponCache(event, coupon);
	}

	/**
	 * 이벤트 캐시에서 이벤트 정보를 가져와서 이벤트 기간 및 시간을 검증합니다.
	 **/
	private void checkIssuableEventPeriodAndTime(final LocalDateTime currentDateTime,
		final EventRedisEntity eventCache) {
		eventCache.availableIssuePeriodAndTime(currentDateTime);
	}

	/**
	 * 검증이 완료된 이후, 쿠폰 발급 요청을 처리하는 메서드 입니다.<br>
	 *
	 * RabbitMQ에 쿠폰 발급 요청 적재 : 선착 순 대기 큐 목록으로서 사용됩니다.</li>
	 * @param issueRequestKey 쿠폰 발급 요청 키
	 */
	private void issueRequest(final IssueRequestKey issueRequestKey) {
		producer.producer(EXCHANGE_NAME, ROUTING_KEY,
			new CouponIssueMessageForQueue(issueRequestKey.getCouponId(), issueRequestKey.getMemberId()));
	}
}
