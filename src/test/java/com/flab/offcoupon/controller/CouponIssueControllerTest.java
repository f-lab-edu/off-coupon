package com.flab.offcoupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.exception.coupon.CouponExceptionHandler;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.event.EventExceptionHandler;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import com.flab.offcoupon.service.coupon_issue.CouponIssueRequestService;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.event.EventErrorMessage.*;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CouponIssueController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)})
class CouponIssueControllerTest {

    private StringBuilder sb = new StringBuilder("{");
    private String URL_SYNC = "/api/v1/event/%s/issues-sync?";

    private String URL_ASYNC = "/api/v1/event/%s/issues-async?";

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CouponIssueRequestService couponIssueRequestService;


    @BeforeEach
    void init() {
        this.mvc = MockMvcBuilders.standaloneSetup(new CouponIssueController(couponIssueRequestService))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                // setControllerAdvice : 컨트롤러에서 발생하는 예외를 처리하기 위해 사용합니다
                .setControllerAdvice(new EventExceptionHandler(), new CouponExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 실패 : 이벤트ID가 존재하지 않을 경우")
    @WithMockUser
    void issue_coupon_fail_invalid_event_id() throws Exception {
        long invalidEventId = 2L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));

        ResponseDTO failResponse = ResponseDTO.getFailResult(EVENT_NOT_EXIST.formatted(invalidEventId));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willThrow(new EventNotFoundException(EVENT_NOT_EXIST.formatted(invalidEventId)));

        // When & then
        mvc.perform(post(URL_SYNC.formatted(invalidEventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", startsWith(failResponse.getMessage())));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 실패 : 쿠폰ID가 존재하지 않을 경우")
    @WithMockUser
    void issue_coupon_fail_invalid_coupon_id() throws Exception {
        long eventId = 1L;
        long invalidCouponId = 2L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(invalidCouponId));
        info.add("memberId", String.valueOf(memberId));

        ResponseDTO failResponse = ResponseDTO.getFailResult(COUPON_NOT_EXIST.formatted(invalidCouponId));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willThrow(new CouponNotFoundException(COUPON_NOT_EXIST.formatted(invalidCouponId)));

        // When & then
        mvc.perform(post(URL_SYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", startsWith(failResponse.getMessage())));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 실패 : 이벤트 기간 설정이 안되어 있을 경우")
    @WithMockUser
    void issue_coupon_fail_null_event_period() throws Exception {
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));

        ResponseDTO failResponse = ResponseDTO.getFailResult(EVENT_PERIOD_IS_NULL.formatted(null, null));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willThrow(new EventPeriodException(EVENT_PERIOD_IS_NULL.formatted(null, null)));

        // When & then
        mvc.perform(post(URL_SYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", startsWith(failResponse.getMessage())));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 실패 : 이벤트 기간이 아닐 경우")
    @WithMockUser
    void issue_coupon_fail_invalid_event_period() throws Exception {
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));

        ResponseDTO failResponse = ResponseDTO.getFailResult(INVALID_EVENT_PERIOD.formatted("2021-01-01", "2021-01-01"));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willThrow(new EventPeriodException(INVALID_EVENT_PERIOD.formatted("2021-01-01", "2021-01-01")));

        // When & then
        mvc.perform(post(URL_SYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", startsWith(failResponse.getMessage())));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 실패 : 이벤트 시간 설정이 안되어 있을 경우")
    @WithMockUser
    void issue_coupon_fail_null_event_time() throws Exception {
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));

        ResponseDTO failResponse = ResponseDTO.getFailResult(EVENT_TIME_IS_NULL.formatted(null, null));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willThrow(new EventTimeException(EVENT_TIME_IS_NULL.formatted(null, null)));

        // When & then
        mvc.perform(post(URL_SYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", startsWith(failResponse.getMessage())));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 실패 : 이벤트 시간이 아닐 경우")
    @WithMockUser
    void issue_coupon_fail_invalid_event_time() throws Exception {
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));

        ResponseDTO failResponse = ResponseDTO.getFailResult(INVALID_EVENT_TIME.formatted("13:00:00","15:00:00"));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willThrow(new EventTimeException(INVALID_EVENT_TIME.formatted("13:00:00","15:00:00")));

        // When & then
        mvc.perform(post(URL_SYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", startsWith(failResponse.getMessage())));
    }

    @Test
    @DisplayName("[SUCCESS] 동기식 쿠폰 발급 성공")
    @WithMockUser
    void sync_issue_coupon_success() throws Exception {
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));
        ResponseDTO successResult = ResponseDTO.getSuccessResult("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
        given(couponIssueRequestService.syncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willReturn(successResult);

        // When & then
        mvc.perform(post(URL_SYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", startsWith(successResult.getMessage())));
    }

    @Test
    @DisplayName("[SUCCESS] 비동기식 쿠폰 발급 성공")
    @WithMockUser
    void async_issue_coupon_success() throws Exception {
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("couponId", String.valueOf(couponId));
        info.add("memberId", String.valueOf(memberId));
        ResponseDTO successResult = ResponseDTO.getSuccessResult("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
        given(couponIssueRequestService.asyncIssueCoupon(any(LocalDateTime.class), any(Long.class), any(Long.class), any(Long.class)))
                .willReturn(successResult);

        // When & then
        mvc.perform(post(URL_ASYNC.formatted(eventId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .params(info))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", startsWith(successResult.getMessage())));
    }
}