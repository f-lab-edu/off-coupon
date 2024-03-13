package com.flab.offcoupon.exception.event;

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
public class EventExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> eventNotFountException(EventNotFoundException ex, HttpServletRequest request) {
        log.info(HTTP_REQUEST, request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(EventPeriodException.class)
    public ResponseEntity<ResponseDTO<String>> eventBadRequestException(EventPeriodException ex, HttpServletRequest request) {
        log.info(HTTP_REQUEST, request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(EventTimeException.class)
    public ResponseEntity<ResponseDTO<String>> eventBadRequestException(EventTimeException ex, HttpServletRequest request) {
        log.info(HTTP_REQUEST, request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }
}
