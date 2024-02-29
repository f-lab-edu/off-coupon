package com.flab.offcoupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.domain.entity.Role;
import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.exception.GlobalExceptionHandler;
import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.MessageFormat;
import java.time.LocalDate;

import static com.flab.offcoupon.exception.member.MemberErrorMessage.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)})
class MemberControllerTest {
    private StringBuilder sb = new StringBuilder("{");
    private String URL = "/api/v1/members/signup";

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;


    @BeforeEach
    void init() {
        this.mvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 이메일이 NULl인 경우")
    @WithMockUser
    void signup_fail_email_null() throws Exception {

        // Given
        sb.append("email=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create(null, "abcabc123", "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(EMAIL_MUST_NOT_EMPTY).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"@naver", "email", "123!naver", "123#email", "123"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 이메일 포맷이 틀린 경우")
    @WithMockUser
    void signup_fail_email(String email) throws Exception {

        // Given
        sb.append("email=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create(email, "abcabc123", "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_EMAIL).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                        .andDo(print());
    }

    @Test
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 비밀번호가 NULl인 경우")
    @WithMockUser
    void signup_fail_password_null() throws Exception {

        // Given
        sb.append("password=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", null, "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(PSWD_MUST_NOT_EMPTY).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234123412", "bcdefgqwer", "SKQLaskd"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 비밀번호 포맷이 틀린 경우")
    @WithMockUser
    void signup_fail_password_format(String password) throws Exception {

        // Given
        sb.append("password=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", password, "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_PSWD_FORMAT).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12ab", "123123asbasbasbasb"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 비밀번호 길이가 틀린 경우")
    @WithMockUser
    void signup_fail_password_length(String password) throws Exception {

        // Given
        sb.append("password=");
        int min = 8;
        int max = 13;
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", password, "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(MessageFormat.format(CHECK_REQUEST_PSWD_LENGTH, min, max)).toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }


    @Test
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 이름이 NULL일 경우")
    @WithMockUser
    void signup_fail_name_null() throws Exception {

        // Given
        sb.append("name=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", "abcabc123", null, LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @Test
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 휴대폰 번호가 NULL일 경우")
    @WithMockUser
    void signup_fail_birthdate_null() throws Exception {

        // Given
        sb.append("phone=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", "abcabc123", "name", LocalDate.parse("2024-12-12"), null, Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(PHONE_MUST_NOT_EMPTY).toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"01012341234","0101234-1234","010-12341234"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 휴대폰 번호 포맷이 틀린 경우")
    @WithMockUser
    void signup_fail_phone(String phone) throws Exception {

        // Given
        sb.append("phone=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", "abcabc123", "name", LocalDate.parse("2024-12-12"), phone, Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_PHONE).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @Test
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 생년월일이 NULL일 경우")
    @WithMockUser
    void signup_fail_phone_null() throws Exception {

        // Given
        sb.append("birthdate=");
        SignupMemberRequestDto invalidSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", "abcabc123", "name", null, "010-1234-1234", Role.ROLE_USER);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(BIRTHDATE_MUST_NOT_EMPTY).toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidSignupMemberRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @Test
    @DisplayName("[SUCCESS] 회원가입 시 유효성 검사 성공")
    @WithMockUser
    void signup_success() throws Exception {

        // Given
        SignupMemberRequestDto validSignupMemberRequestDto = SignupMemberRequestDto.create("gildong@naver.com", "abcabc123", "name", LocalDate.parse("2024-12-12"), "010-1234-1234", Role.ROLE_USER);
        ResponseDTO successResponse = ResponseDTO.getSuccessResult(validSignupMemberRequestDto);
        given(memberService.signUp(any())).willReturn(successResponse);

        // When
        ResultActions result = mvc.perform(post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignupMemberRequestDto)))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}