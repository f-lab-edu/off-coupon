DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS coupon_issue;

CREATE TABLE member
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '회원 식별자',
    email VARCHAR(40) NOT NULL COMMENT '회원 이메일',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호',
    name VARCHAR(40) NOT NULL COMMENT '회원 이름',
    birthdate DATE NOT NULL COMMENT '생년월일',
    phone VARCHAR(40) NOT NULL COMMENT '휴대폰 번호',
    role VARCHAR(50) NOT NULL COMMENT '회원 권한',
    created_at DATETIME NOT NULL COMMENT '데이터 생성일',
    updated_at DATETIME NOT NULL COMMENT '데이터 변경일'
);

CREATE TABLE event
(
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '이벤트 식별자',
    category               VARCHAR(100) NOT NULL COMMENT '이벤트 카테고리',
    description            VARCHAR(255) NOT NULL COMMENT '이벤트 설명',
    start_date             DATE         NOT NULL COMMENT '이벤트 시작일',
    end_date               DATE         NOT NULL COMMENT '이벤트 종료일',
    daily_issue_start_time VARCHAR(20)  NULL COMMENT '당일 쿠폰 발행 시작시간 e.g. "13:00:00"/ null일 경우 무한 발행',
    daily_issue_end_time   VARCHAR(20)  NULL COMMENT '당일 쿠폰 발행 종료시간 e.g. "15:00:00" / null일 경우 무한 발행',
     created_at DATETIME NOT NULL COMMENT '데이터 생성일',
     updated_at DATETIME NOT NULL COMMENT '데이터 변경일'
);

CREATE TABLE coupon
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '쿠폰 식별자',
    event_id            BIGINT UNSIGNED NULL COMMENT '이벤트 식별자 / NULL일 경우 이벤트와 관련 없는 쿠폰(e.g. 회원가입 쿠폰)',
    discount_type       VARCHAR(40)     NOT NULL COMMENT '정액, 정률 등',
    discount_rate       BIGINT UNSIGNED NULL COMMENT '정률 할인',
    discount_price      BIGINT UNSIGNED NULL COMMENT '정액 할인',
    coupon_type         VARCHAR(50)     NOT NULL COMMENT '선착순 쿠폰, 회원가입 쿠폰 등..',
    max_quantity        BIGINT UNSIGNED NULL COMMENT '무제한 발행일 경우 NULL',
    issued_quantity     BIGINT UNSIGNED NULL COMMENT '무제한 발행일 경우 NULL',
    validate_start_date DATETIME        NOT NULL COMMENT '모든 쿠폰은 유효 시간이 있어야한다는 제약 사항 존재',
    validate_end_date   DATETIME        NOT NULL COMMENT '모든 쿠폰은 유효 시간이 있어야한다는 제약 사항 존재',
    created_at          DATETIME        NOT NULL COMMENT '데이터 생성일',
    updated_at          DATETIME        NOT NULL COMMENT '데이터 변경일'
);

CREATE TABLE coupon_issue
(
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '쿠폰 발행 기록',
    member_id       BIGINT UNSIGNED NOT NULL COMMENT '쿠폰 ID',
    coupon_id       BIGINT UNSIGNED NOT NULL COMMENT '회원 ID',
    coupon_status              VARCHAR(50)     NOT NULL DEFAULT 'NOT_ACTIVE' COMMENT '유효일 전 : NOT_ACTIVE / 유효기간 : ACTIVE / 사용완료 : USED / 만료 : EXPIRED',
    created_at      DATETIME        NOT NULL COMMENT '데이터 생성일',
    updated_at      DATETIME        NOT NULL COMMENT '데이터 변경일'
);

