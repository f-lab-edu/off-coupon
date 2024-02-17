package com.flab.offcoupon.config.security.handler.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.flab.offcoupon.exception.ErrorMessage.CHECK_EMAIL_OR_PSWD;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        logErrorDetails(exception, request.getRequestURI());
        if (exception instanceof BadCredentialsException) {
            handleFailureResponse(response);
        }
    }
    private void logErrorDetails(AuthenticationException accessDeniedException, String requestUri) {
        log.error("Not Authorized Request in AuthenticationFailureHandler ", accessDeniedException);
        log.error("Request Uri: {}", requestUri);
    }
    private void handleFailureResponse(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(ResponseDTO.getFailResult(CHECK_EMAIL_OR_PSWD)));
    }
}
