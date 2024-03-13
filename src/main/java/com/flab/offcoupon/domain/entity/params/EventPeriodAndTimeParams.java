package com.flab.offcoupon.domain.entity.params;

import java.time.LocalDate;


/**
 * 이 파일은 생성자의 파라미터를 줄이기 위해 만들어졌습니다.
 * MyBatis에서 @Builder 어노테이션을 사용할 경우 에러가 발생하기 때문에 생성자만 사용하였습니다.
 * MyBatis와 @Builder 어노테이션을 함께 사용할 경우 기본 생성자가 필요하지만, 불변 객체로 엔티티를 구성했기 때문에 기본 생성자를 만들지 않았습니다.
 * 따라서 레코드(record) 클래스를 사용하여 생성자 파라미터를 간결하게 정의하였습니다.
 */
public record EventPeriodAndTimeParams (
        LocalDate startDate,
        LocalDate endDate,
        String dailyIssueStartTime,
        String dailyIssueEndTime
) {
    public EventPeriodAndTimeParams(LocalDate startDate, LocalDate endDate, String dailyIssueStartTime, String dailyIssueEndTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyIssueStartTime = dailyIssueStartTime;
        this.dailyIssueEndTime = dailyIssueEndTime;
    }
}
