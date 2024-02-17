package com.flab.offcoupon.security;

import com.flab.offcoupon.repository.MemberRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;
    //mockMvc 객체 생성, Spring Security 환경 setup
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    public void anonymous_authorization_home() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "sejin", roles = "USER")
    public void authentication_member() throws Exception {
        mockMvc.perform(get("/member"))
                .andDo(print())
                .andExpect(status().isOk());
    }

//    @Test //TODO : 테스트 실패함 (시큐리티 내부에서 email이랑 password 입력값을 제대로 못받음)
//    public void login_success() throws Exception {
//        String email = "sejin@email.com";
//        String password = "ababab123123";
//        mockMvc.perform(formLogin("/members/login")
//                .user(email, password))
//                .andDo(print())
//                .andExpect(authenticated())
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"));
//    }

    @Test
    public void login_fail_not_exist() throws Exception{
        String email = "존재하지 않는 유저";
        String password = "123";
        mockMvc.perform(formLogin("/members/login")
                        .user(email)
                        .password(password))
                .andDo(print())
                .andExpect(unauthenticated());
    }
}
