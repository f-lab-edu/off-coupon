package com.flab.offcoupon.exception.statistics;

import com.flab.offcoupon.util.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.flab.offcoupon.exception.GlobalExceptionHandler.HTTP_REQUEST;

@Slf4j
@RestControllerAdvice
public class StatisticsExceptionHandler {
    @ExceptionHandler(LocalDateBadRequestException.class)
    public ResponseEntity<ResponseDTO<String>> couponNotFountException(LocalDateBadRequestException ex, HttpServletRequest request) {
        log.info(HTTP_REQUEST, request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }
}
