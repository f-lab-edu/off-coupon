package com.flab.offcoupon.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 로그인 실패 시 JSON 응답
        if (exception instanceof BadCredentialsException) {
            response.setCharacterEncoding("UTF-8");  // 문자 인코딩을 UTF-8로 설정
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println(
                    new ObjectMapper().writeValueAsString(
                            ResponseDTO.getFailResult("아이디 또는 비밀번호가 일치하지 않습니다.")));

        }

        setDefaultFailureUrl("/login");
        super.onAuthenticationFailure(request, response, exception);
    }
}
