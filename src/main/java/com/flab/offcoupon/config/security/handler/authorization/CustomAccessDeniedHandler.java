package com.flab.offcoupon.config.security.handler.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.flab.offcoupon.exception.ErrorMessage.AUTHORIZED_BUT_NOT_AUTHENTICATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        logErrorDetails(accessDeniedException, request.getRequestURI());

        configureResponse(response);

        writeErrorResponse(response);
    }

    private void logErrorDetails(AccessDeniedException accessDeniedException, String requestUri) {
        log.error("Not Authenticated Request in AccessDeniedHandler", accessDeniedException);
        log.error("Request Uri: {}", requestUri);
    }

    private void configureResponse(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        response.getWriter().write(objectMapper.writeValueAsString(ResponseDTO.getFailResult(AUTHORIZED_BUT_NOT_AUTHENTICATED)));
    }
}
