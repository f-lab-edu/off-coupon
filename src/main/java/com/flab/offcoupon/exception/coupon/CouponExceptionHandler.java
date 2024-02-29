package com.flab.offcoupon.exception.coupon;

import com.flab.offcoupon.util.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CouponExceptionHandler {
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
