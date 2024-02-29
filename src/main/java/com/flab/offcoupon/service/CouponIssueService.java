package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.Coupon;
import com.flab.offcoupon.domain.CouponIssue;
import com.flab.offcoupon.domain.Event;
import com.flab.offcoupon.domain.CouponIssueCheckVo;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.CouponIssueRepository;
import com.flab.offcoupon.repository.CouponRepository;
import com.flab.offcoupon.repository.EventRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.ErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.coupon.ErrorMessage.DUPLICATED_COUPON;
import static com.flab.offcoupon.exception.event.ErrorMessage.EVENT_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final EventRepository eventRepository;
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) {
        // 이벤트 기간 및 시간 검증
        checkEventPeriodAndTime(eventId, currentDateTime);
        // 발급된 쿠폰 수 증가 (Coupon 테이블의 issuedQuantity)
        increaseIssuedCouponQuantity(couponId);
        // 쿠폰 발급 이력 저장 (CouponIssue 테이블) 및 중복 발급 제한
        saveCouponIssue(memberId, couponId, currentDateTime);
        return ResponseDTO.getSuccessResult("Test");
    }


    private void checkEventPeriodAndTime(long eventId, LocalDateTime currentDateTime) {
        Event event = findEvent(eventId);
        event.availableIssuePeriodAndTime(currentDateTime);
    }

    @Transactional
    public void increaseIssuedCouponQuantity(long couponId) {
        Coupon existingCoupon = findCoupon(couponId);
        Coupon updatecoupon = existingCoupon.increaseIssuedQuantity(existingCoupon);
        couponRepository.increaseIssuedQuantity(updatecoupon);
    }

    @Transactional
    public void saveCouponIssue(long memberId, long couponId, LocalDateTime currentDateTime) {
        LocalDate currentDate = currentDateTime.toLocalDate();
        checkAlreadyIssueHistory(memberId, couponId, currentDate);
        CouponIssue couponIssue = CouponIssue.create(memberId, couponId);
        couponIssueRepository.save(couponIssue);
    }
    @Transactional(readOnly = true)
    public void checkAlreadyIssueHistory(long memberId, long couponId, LocalDate currentDate) {
        if(couponIssueRepository.existCouponIssue(new CouponIssueCheckVo(memberId, couponId, currentDate))) {
            throw new DuplicatedCouponException(DUPLICATED_COUPON.formatted(memberId, couponId));
        }
    }

    @Transactional(readOnly = true)
    public Event findEvent(long eventId) {
        return eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(eventId)));
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }
}
