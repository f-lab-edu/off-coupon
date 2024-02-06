package com.flab.offcoupon.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class ResponseDTO<T> {

    private final Status status;
    private final T data;
    private final String message;

    /*
     * 응답 성공
     */
    public static <T> ResponseDTO getSuccessResult (T data) {
        return ResponseDTO.builder()
                .status(Status.SUCCESS)
                .data(data)
                .message("요청 성공")
                .build();
    }

    /*
     * 실패
     */
    public static ResponseDTO getFailResult(String message) {
        return ResponseDTO.builder()
                .status(Status.FAIL)
                .data(null)
                .message(message)
                .build();
    }


    private enum Status {
        SUCCESS,
        FAIL
    }

}
