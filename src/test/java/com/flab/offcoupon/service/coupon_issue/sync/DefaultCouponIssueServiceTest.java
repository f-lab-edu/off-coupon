package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.setup.SetupUtils;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.DUPLICATED_COUPON;
import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 *  DefaultCouponIssueService 클래스의 issueCoupon 메서드를 테스트하는 클래스입니다.
 *  Mockito를 사용하여 테스트를 진행할 수 없는 이유
 * <ul>
 *     <li>issueCoupon 메소드에는 내부적으로 이벤트 테이블을 캐싱을 하는 로직이 담겨 있습니다.</li>
 *     <li>이벤트 테이블을 캐싱하는 로직은 EventCacheService 클래스에 구현되어 있습니다.</li>
 *     <li> @Cacheable 어노테이션이 적용된 메소드는 Spring의 캐시 메커니즘을 사용하기 때문에 Mock 객체를 사용하여 테스트할 수 없습니다.</li>
 *     <li> 따라서 Spring의통합 테스트 환경을 구성하기 위해 @SpringBootTest 어노테이션을 적용했습니다</li>
 *     <li> @IntegrationTest 어노테이션의 경우 통합테스트로 사용되었지만, deprecated되고 1.4버전 부터 @SpringBootTest를 통합테스트로 사용합니다</li>
 * </ul>
 * @See <a href="https://docs.spring.io/spring-boot/docs/1.4.x/api/org/springframework/boot/test/IntegrationTest.html">Annotation IntegrationTest</a>
 * @See <a href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html">Annotation SpringBootTest</a>
 */
@SpringBootTest
@Transactional
class DefaultCouponIssueServiceTest {

    @Autowired
    private DefaultCouponIssueService defaultCouponIssueService;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponRepository couponRepository;
    private SetupUtils setupUtils = new SetupUtils();

    @BeforeEach
    void setUp() {
        setupUtils.setUpEventAndCoupon(eventRepository, couponRepository);
    }

    @Transactional
    @Test
    @DisplayName("[ERROR] 쿠폰 발급 - 이벤트 식별자가 존재하지 않으면 Exception 발생")
    void issueCoupon_fail_with_invalid_eventId() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long invalidEventId = 2L;
        long couponId = 1L;
        long memberId = 1L;
        // when
        assertThatThrownBy(() -> defaultCouponIssueService.issueCoupon(currentDateTime, invalidEventId, couponId, memberId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessage(EVENT_NOT_EXIST.formatted(invalidEventId));
    }
    @Test
    @DisplayName("[ERROR] 쿠폰 발급 - 쿠폰 식별자가 존재하지 않으면 Exception 발생")
    void issueCoupon_fail_with_invalid_couponId() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long eventId = 1L;
        long invalidCouponId = 2L;
        long memberId = 1L;
        // when
        assertThatThrownBy(() -> defaultCouponIssueService.issueCoupon(currentDateTime, eventId, invalidCouponId, memberId))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessage(COUPON_NOT_EXIST.formatted(invalidCouponId));
    }

    @Transactional
    @Test
    @DisplayName("[ERROR] 쿠폰 발급 - 중복 요청의 경우 Exception 발생")
    void issueCoupon_fail_with_duplicated_request() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        CouponIssue couponIssue = CouponIssue.create(memberId, couponId);
        couponIssueRepository.save(couponIssue);
        // when
        assertThatThrownBy(() -> defaultCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId))
                .isInstanceOf(DuplicatedCouponException.class)
                .hasMessage(DUPLICATED_COUPON.formatted(memberId, couponId));
    }

    @Transactional
    @Test
    @DisplayName("[SUCCESS] 쿠폰 발급 - 쿠폰 발급 성공")
    void issueCoupon_success() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        // when
        ResponseDTO responseDTO = defaultCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        assertThat(responseDTO.getData()).isEqualTo("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
    }
}