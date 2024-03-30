package com.flab.offcoupon.setup;

import com.flab.offcoupon.domain.entity.*;
import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.repository.mysql.MemberRepository;
import com.flab.offcoupon.service.MemberService;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 테스트를 위한 추상화된 공통 유틸리티 클래스입니다.
 * 회원 가입, 이벤트 및 쿠폰 설정 등의 테스트 시 공통적으로 사용되는 기능을 제공합니다.
 */
public class SetupInitializer {
    private SignupMemberRequestDto testSignupMemberDto;
    private EventRepository eventRepository;
    private CouponRepository couponRepository;
    /**
     * 기본 생성자 - 테스트용 회원 가입 DTO를 초기화합니다.
     */
    public SetupInitializer() {
        this.testSignupMemberDto = SignupMemberRequestDto.create(
                "test@gmail.com",
                "ababab123123",
                "이름",
                LocalDate.now(),
                "01012345678",
                Role.ROLE_USER
        );
    }
    /**
     * 생성자 - 이벤트 저장소와 쿠폰 저장소를 초기화합니다.
     *
     * @param eventRepository 이벤트 저장소
     * @param couponRepository 쿠폰 저장소
     */
    public SetupInitializer(EventRepository eventRepository, CouponRepository couponRepository) {
        this.eventRepository = eventRepository;
        this.couponRepository = couponRepository;
    }

    /**
     * 주어진 MemberService를 사용하여 테스트용 회원을 가입시킵니다.
     *
     * @param memberService 회원 가입을 처리할 MemberService 객체
     */
    public void setUpMember(MemberService memberService) {
        memberService.signUp(testSignupMemberDto);
    }
    /**
     * 주어진 MemberRepository를 사용하여 테스트용 회원을 저장합니다.
     *
     * @param memberRepository 회원을 저장할 MemberRepository 객체
     */
    public void setUpMember(MemberRepository memberRepository) {
        memberRepository.save(Member.create(
                testSignupMemberDto.getEmail(),
                testSignupMemberDto.getPassword(),
                testSignupMemberDto.getName(),
                testSignupMemberDto.getBirthdate(),
                testSignupMemberDto.getPhone(),
                testSignupMemberDto.getRole()));
    }
    /**
     * 이벤트와 쿠폰을 설정하여 저장합니다.
     */
    public void setUpEventAndCoupon() {
        Event event = new Event(
                1L,
                "바디케어",
                "바디케어 전품목 이벤트",
                LocalDate.now(),
                LocalDate.now(),
                "13:00:00",
                "15:00:00",
                LocalDateTime.now(),
                LocalDateTime.now());
        eventRepository.save(event);

        Coupon coupon = new Coupon(
                1L,
                1L,
                DiscountType.PERCENT,
                50L,
                null,
                CouponType.FIRST_COME_FIRST_SERVED,
                500L,
                0L,
                LocalDateTime.now().plusMonths(1L),
                LocalDateTime.now().plusMonths(2L),
                LocalDateTime.now(),
                LocalDateTime.now());
        couponRepository.save(coupon);
    }
    /**
     * 주어진 매개변수로 이벤트와 쿠폰을 설정하여 저장합니다.
     *
     * @param startDate 이벤트 시작일
     * @param endDate 이벤트 종료일
     * @param startTime 이벤트 시작 시간
     * @param endTime 이벤트 종료 시간
     */
    public void setUpEventAndCouponWithParams(LocalDate startDate, LocalDate endDate, String startTime, String endTime) {
        Event event = new Event(
                2L,
                "바디케어2",
                "바디케어 전품목 이벤트2",
                startDate,
                endDate,
                startTime,
                endTime,
                LocalDateTime.now(),
                LocalDateTime.now());
        eventRepository.save(event);

        Coupon coupon = new Coupon(
                2L,
                1L,
                DiscountType.PERCENT,
                50L,
                null,
                CouponType.FIRST_COME_FIRST_SERVED,
                500L,
                0L,
                LocalDateTime.now().plusMonths(1L),
                LocalDateTime.now().plusMonths(2L),
                LocalDateTime.now(),
                LocalDateTime.now());
        couponRepository.save(coupon);
    }
}
