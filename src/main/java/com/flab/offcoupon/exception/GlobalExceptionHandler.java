package com.flab.offcoupon.exception;

import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.exception.member.MemberNotFoundException;
import com.flab.offcoupon.util.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public final class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
            ) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        });
        return ResponseEntity.badRequest().body(ResponseDTO.getFailResult(fieldErrors.toString()));
    }

    @ExceptionHandler(MemberBadRequestException.class)
    public ResponseEntity<ResponseDTO> badRequestException(MemberBadRequestException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ResponseDTO> memberNotFountException(MemberNotFoundException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ResponseDTO> eventNotFountException(EventNotFoundException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(EventPeriodException.class)
    public ResponseEntity<ResponseDTO> eventBadRequestException(EventPeriodException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(EventTimeException.class)
    public ResponseEntity<ResponseDTO> eventBadRequestException(EventTimeException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ResponseDTO> couponNotFountException(CouponNotFoundException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(CouponQuantityException.class)
    public ResponseEntity<ResponseDTO> couponBadRequestException(CouponQuantityException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }

    @ExceptionHandler(DuplicatedCouponException.class)
    public ResponseEntity<ResponseDTO> couponBadRequestException(DuplicatedCouponException ex, HttpServletRequest request) {
        log.info("> Http Method : {},  URI : {}, msg : {}, status : {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.getFailResult(ex.getMessage()));
    }
}
