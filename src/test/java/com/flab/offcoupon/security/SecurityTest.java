package com.flab.offcoupon.security;

import com.flab.offcoupon.service.MemberService;
import com.flab.offcoupon.setup.SetupUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityTest {
    private static final String MEMBER_LOGIN_URL = "/api/v1/members/login";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberService memberService;

    private SetupUtils setupUtils;

    //mockMvc 객체 생성, Spring Security 환경 setup
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
        setupUtils = new SetupUtils();
        setupUtils.setUpMember(memberService);
    }

    @Test
    @DisplayName("[SUCCESS] 비회원 권한으로 홈 접근")
    @WithAnonymousUser
    void anonymous_authorization_home() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("[SUCCESS] 회원 권한으로 홈 접근")
    @WithMockUser(authorities = "USER")
    void authentication_member() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[SUCCESS] 로그인 성공")
    void login_success() throws Exception {
        String email = "test@gmail.com";
        String password = "ababab123123";
        mockMvc.perform(formLogin(MEMBER_LOGIN_URL)
                        .user("email",email)
                        .password("password",password))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("[ERROR] 접근 권한 없음 : USER가 ADMIN 권한으로 접근")
    @WithMockUser(authorities="USER")
    void endpoint_with_user_authority_authorized() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("[ERROR] 로그인 실패 : 비밀번호 불일치")
    public void login_fail_with_wrong_pwsd() throws Exception {
        String email = "test@gmail.com";
        String password = "ab";
        mockMvc.perform(formLogin(MEMBER_LOGIN_URL)
                        .user("email",email)
                        .password("password",password))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[ERROR] 로그인 실패 : 존재하지 않는 유저")
    void login_fail_not_exist() throws Exception{
        String email = "존재하지 않는 유저";
        String password = "123";
        mockMvc.perform(formLogin(MEMBER_LOGIN_URL)
                        .user("email", email)
                        .password("password", password))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("[SUCCESS] 로그아웃")
    void logout() throws Exception{
        mockMvc.perform(SecurityMockMvcRequestBuilders.logout("/api/v1/members/logout"))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl(MEMBER_LOGIN_URL));
    }
}
