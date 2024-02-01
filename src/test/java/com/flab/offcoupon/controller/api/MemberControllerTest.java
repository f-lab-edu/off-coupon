package com.flab.offcoupon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.flab.offcoupon.exception.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest
    @ValueSource(strings = {"@naver", "email", "123!naver", "123#email", "123"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 이메일 포맷이 틀린 경우")
    @WithMockUser
    void signup_fail_email(String email) throws Exception {

        // Given
        sb.append("email=");
        MemberMapperDTO invalidMemberMapperDTO = MemberMapperDTO.create(email, "abcabc123", "name", "2024-12-12", "010-1234-1234");
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_EMAIL).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidMemberMapperDTO))
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
        MemberMapperDTO invalidMemberMapperDTO = MemberMapperDTO.create("gildong@naver.com", password, "name", "2024-12-12", "010-1234-1234");
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_PSWD_FORMAT).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidMemberMapperDTO))
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
        MemberMapperDTO invalidMemberMapperDTO = MemberMapperDTO.create("gildong@naver.com", password, "name", "2024-12-12", "010-1234-1234");
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(MessageFormat.format(CHECK_REQUEST_PSWD_LENGTH, min, max)).toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidMemberMapperDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @Test
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 이름이 없을 경우")
    @WithMockUser
    void signup_fail_password_length() throws Exception {

        // Given
        sb.append("name=");
        MemberMapperDTO invalidMemberMapperDTO = MemberMapperDTO.create("gildong@naver.com", "abcabc123", null, "2024-12-12", "010-1234-1234");
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidMemberMapperDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"01012341234","0101234-1234","010-12341234"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 전화번호 포맷이 틀린 경우")
    @WithMockUser
    void signup_fail_phone(String phone) throws Exception {

        // Given
        sb.append("phone=");
        MemberMapperDTO invalidMemberMapperDTO = MemberMapperDTO.create("gildong@naver.com", "abcabc123", "name", "2024-12-12", phone);
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_PHONE).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidMemberMapperDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",startsWith(sb.toString())))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"20241212", "1234-556-177", "12-12-12"})
    @DisplayName("[ERROR] 회원가입 시 유효성 검사 : 생년월일 포맷이 틀린 경우")
    @WithMockUser
    void signup_fail_birthDate(String birthDate) throws Exception {

        // Given
        sb.append("birthDate=");
        MemberMapperDTO invalidMemberMapperDTO = MemberMapperDTO.create("gildong@naver.com", "abcabc123", "name", birthDate, "010-1234-1234");
        ResponseDTO failResponse = ResponseDTO.getFailResult(sb.append(CHECK_REQUEST_BIRTHDATE).append("}").toString());
        given(memberService.signUp(any())).willReturn(failResponse);

        // When & then
        mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(invalidMemberMapperDTO))
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
        MemberMapperDTO validMemberMapperDTO = MemberMapperDTO.create("gildong@naver.com", "abcabc123", "name", "2024-12-12", "010-1234-1234");
        ResponseDTO successResponse = ResponseDTO.getSuccessResult(validMemberMapperDTO);
        given(memberService.signUp(any())).willReturn(successResponse);

        // When
        ResultActions result = mvc.perform(post("/members/signup")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberMapperDTO)))
                .andExpect(status().isOk())
                .andDo(print());

        // Additional AssertJ Assertions
        assertThat(result.andReturn().getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(successResponse));
    }
}