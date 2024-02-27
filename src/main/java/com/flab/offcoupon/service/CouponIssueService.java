package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.Coupon;
import com.flab.offcoupon.domain.CouponIssue;
import com.flab.offcoupon.domain.Event;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.CouponIssueRepository;
import com.flab.offcoupon.repository.CouponRepository;
import com.flab.offcoupon.repository.EventRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.event.ErrorMessage.EVENT_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final EventRepository eventRepository;

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public ResponseDTO issueCoupon(long eventId, long couponId, long memberId) {
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 01, 13, 0, 0);
        checkEventPeriodAndTime(eventId, currentDateTime);
        increaseIssuedCouponQuantity(couponId);
        saveCouponIssue(memberId, couponId);
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
    public void saveCouponIssue(long memberId, long couponId) {
        checkAlreadyIssueHistory(memberId, couponId);
        CouponIssue couponIssue = CouponIssue.create(memberId, couponId);
        couponIssueRepository.save(couponIssue);
    }

    private void checkAlreadyIssueHistory(long memberId, long couponId) {
        if(couponIssueRepository.existCouponIssueByMemberIdAndCouponId(memberId, couponId)) {
            throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
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
                .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(couponId)));
    }
}
