package com.flab.offcoupon.service.coupon_issue.async.consumer;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CountByCouponIdVo;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.exception.coupon.CouponIssueException;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponIssueMessageHandler {

	private final CouponRepository couponRepository;
	private final CouponIssueRepository couponIssueRepository;

	/**
	 * 각 쿠폰 발행 이력을 저장합니다.
	 *
	 * @param message 메시지를 통해 받은 쿠폰 발행 정보
	 */
	@Transactional
	public void saveEachCouponIssueHistory(CouponIssueMessageForQueue message) {
		CouponIssue couponIssue = CouponIssue.create(message.memberId(), message.couponId(), false);
		couponIssueRepository.save(couponIssue);
		log.info("쿠폰 히스토리 저장 완료");
	}

	/**
	 * MySQL에 저장된 총 발행된 수량을 업데이트하고, <br>
	 * Redis에 저장된 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
	 *
	 */
	@Transactional
	public void updateTotalIssuedCouponAndCheckFlagForCompletedIssue() {
		List<CountByCouponIdVo> countByCouponIdVo = countTotalCouponIssueForToday();
		if (!countByCouponIdVo.isEmpty()) {
			totalUpdateIssuedCouponAndDeleteRequest(countByCouponIdVo);
			updateCheckFlagAboutCompletedCouponIssue();
		}
	}

	/**
	 * MySQL에 저장된 총 발행된 수량을 업데이트하고, <br>*
	 * @param countByCouponIdVoList 오늘 발급된 쿠폰의 총 발급 수량 및 쿠폰 ID List
	 */
	private void totalUpdateIssuedCouponAndDeleteRequest(List<CountByCouponIdVo> countByCouponIdVoList) {
		countByCouponIdVoList.forEach(countByCouponIdVo -> {
			totalUpdateIssuedQuantity(countByCouponIdVo);
		});
	}

	/**
	 * 총 발행된 쿠폰 수량을 업데이트합니다.
	 */
	private void totalUpdateIssuedQuantity(CountByCouponIdVo countByCouponIdVo) {
		Coupon coupon = couponRepository.getCouponById(countByCouponIdVo.couponId());
		couponRepository.updateTotalIssuedCouponQuantity(
			new UpdateTotalIssuedQuantityVo(countByCouponIdVo.couponId(),
				coupon.getIssuedQuantity() + countByCouponIdVo.count()));
	}

	/**
	 * 오늘 발급된 쿠폰의 총 발급 수량을 확인합니다.
	 * @return List<CountByCouponIdVo> 오늘 발급된 쿠폰의 총 발급 수량 및 쿠폰 ID List
	 */
	private List<CountByCouponIdVo> countTotalCouponIssueForToday() {
		// 쿠폰 발급 이력을 확인하는 로직
		List<CountByCouponIdVo> countByCouponIdVo = couponIssueRepository.countCouponIdForToday();
		return countByCouponIdVo;
	}

	/**
	 * 쿠폰 발급 완료 후 반정규화 칼럼(Coupon테이블의 issued_quantity)를 업데이트 했음을 checkFlag에 반영합니다.
	 */
	private void updateCheckFlagAboutCompletedCouponIssue() {
		List<Long> couponIssueIdListForToday = couponIssueRepository.couponIssueIdListForToday();
		couponIssueIdListForToday.forEach(ids -> {
			CouponIssue couponIssue = couponIssueRepository.findCouponIssueById(ids)
				.orElseThrow(() -> new CouponIssueException(COUPON_ISSUE_NOT_EXIST));
			couponIssueRepository.updateCheckFlag(couponIssue.updateCheckFlag(couponIssue));
		});
	}
}
