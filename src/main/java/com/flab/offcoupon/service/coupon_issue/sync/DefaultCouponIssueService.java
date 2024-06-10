package com.flab.offcoupon.service.coupon_issue.sync;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CouponIssueCheckVo;
import com.flab.offcoupon.dto.request.IssueRequestParameter;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.service.cache.EventCacheService;
import com.flab.offcoupon.util.ResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 동기적으로 쿠폰을 발급하는 서비스 클래스입니다.
 * <p>이 클래스는 요청이 들어오자마자 MySQL에 쿠폰 수량(issuedQuantity)을 증가시키고, 쿠폰 발급 이력을 저장합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultCouponIssueService {

	private final EventCacheService eventCacheService;
	private final CouponRepository couponRepository;
	private final CouponIssueRepository couponIssueRepository;
	private final CouponIssueTransactionalService transactionalService;

	@Transactional
	public ResponseDTO<String> issueCoupon(LocalDateTime currentDateTime, IssueRequestParameter requestParameter) {
		// 이벤트(Event 테이블) 기간 및 시간 검증
		checkEventPeriodAndTime(requestParameter.getEventId(), currentDateTime);
		// 쿠폰 조회 및 발급된 쿠폰 수 증가 (Coupon 테이블의 issuedQuantity)
		increaseIssuedCouponQuantity(requestParameter.getCouponId());
		// 중복 발급 제한 및 쿠폰 발급 이력 저장 (CouponIssue 테이블)
		saveCouponIssue(requestParameter.getMemberId(), requestParameter.getCouponId(), currentDateTime);
		return ResponseDTO.getSuccessResult("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s"
			.formatted(requestParameter.getMemberId(), requestParameter.getCouponId()));
	}

	public void checkEventPeriodAndTime(long eventId, LocalDateTime currentDateTime) {
		// 이벤트 조회 및 기간 및 시간 검증
		EventRedisEntity event = eventCacheService.getEvent(eventId);
		event.availableIssuePeriodAndTime(currentDateTime);
	}

	public void increaseIssuedCouponQuantity(long couponId) {
		// 쿠폰 조회
		Coupon existingCoupon = couponRepository.getCouponById(couponId);
		// 조회된 쿠폰의 반정규화 칼럼인 issuedQuantity 증가
		Coupon updatecoupon = existingCoupon.increaseIssuedQuantity(existingCoupon);
		// 데이터 베이스에 반영
		couponRepository.increaseIssuedQuantity(updatecoupon);
	}

	public void saveCouponIssue(long memberId, long couponId, LocalDateTime currentDateTime) {
		checkAlreadyIssueHistory(memberId, couponId, currentDateTime);
		CouponIssue couponIssue = CouponIssue.create(memberId, couponId, true);
		couponIssueRepository.save(couponIssue);
	}

	private void checkAlreadyIssueHistory(long memberId, long couponId, LocalDateTime currentDateTime) {
		LocalDate currentDate = currentDateTime.toLocalDate();
		if (couponIssueRepository.existCouponIssue(new CouponIssueCheckVo(memberId, couponId, currentDate))) {
			throw new DuplicatedCouponException(DUPLICATED_COUPON.formatted(memberId, couponId));
		}
	}

	private Coupon findCoupon(long couponId) {
		return couponRepository.getCouponById(couponId);
	}
}
