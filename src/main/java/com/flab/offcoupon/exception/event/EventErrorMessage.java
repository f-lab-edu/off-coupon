package com.flab.offcoupon.exception.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventErrorMessage {
    public static final String EVENT_NOT_EXIST = "존재하지 않는 이벤트입니다. eventId : %s";
    public static final String INVALID_EVENT_PERIOD = "이벤트 기간이 아닙니다. startDate : %s, startDate : %s";
    public static final String INVALID_EVENT_TIME =  "이벤트 시간이 아닙니다. dailyIssueStartTime : %s, dailyIssueEndTime : %s";
    public static final String EVENT_PERIOD_IS_NULL = "이벤트 기간 설정이 되어있지 않습니다. startDate : %s, startDate : %s";
    public static final String EVENT_TIME_IS_NULL =  "이벤트 시간 설정이 되어있지 않습니다. dailyIssueStartTime : %s, dailyIssueEndTime : %s";
}
