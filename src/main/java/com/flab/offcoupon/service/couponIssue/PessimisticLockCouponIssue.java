package com.flab.offcoupon.service.couponIssue;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.domain.vo.couponissue.CouponIssueCheckVo;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.CouponIssueRepository;
import com.flab.offcoupon.repository.CouponRepository;
import com.flab.offcoupon.repository.EventRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.DUPLICATED_COUPON;
import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;

@RequiredArgsConstructor
@Service
@Slf4j
public class PessimisticLockCouponIssue implements CouponIssueFacade {

    private final EventRepository eventRepository;
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    @Transactional
    @Override
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) {
        // 이벤트(Event 테이블) 기간 및 시간 검증
        checkEventPeriodAndTime(eventId, currentDateTime);
        // 쿠폰 조회 및 발급된 쿠폰 수 증가 (Coupon 테이블의 issuedQuantity)
        increaseIssuedCouponQuantity(couponId);
        // 중복 발급 제한 및 쿠폰 발급 이력 저장 (CouponIssue 테이블)
        saveCouponIssue(memberId, couponId, currentDateTime);
        return ResponseDTO.getSuccessResult("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
    }


    private void checkEventPeriodAndTime(long eventId, LocalDateTime currentDateTime) {
        Event event = findEvent(eventId);
        event.availableIssuePeriodAndTime(currentDateTime);
    }

    @Transactional
    public void increaseIssuedCouponQuantity(long couponId) {
        Coupon existingCoupon = findCouponPessimisticLock(couponId);
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
        if (couponIssueRepository.existCouponIssue(new CouponIssueCheckVo(memberId, couponId, currentDate))) {
            throw new DuplicatedCouponException(DUPLICATED_COUPON.formatted(memberId, couponId));
        }
    }

    @Transactional(readOnly = true)
    public Event findEvent(long eventId) {
        return eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(eventId)));
    }

    @Transactional(readOnly = true)
    public Coupon findCouponPessimisticLock(long couponId) {
        return couponRepository.findCouponByIdPessimisticLock(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }
}