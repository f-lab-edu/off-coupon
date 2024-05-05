
# USE off_coupon;
#
# GRANT ALL PRIVILEGES ON off_coupon.* TO 'test'@'%' IDENTIFIED BY '<password>';
# FLUSH PRIVILEGES;

DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS coupon_issue;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS order_coupon;

CREATE TABLE member
(
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '회원 식별자',
    email      VARCHAR(255) NOT NULL COMMENT '회원 이메일',
    password   VARCHAR(255) NOT NULL COMMENT '비밀번호',
    name       VARCHAR(100) NOT NULL COMMENT '회원 이름',
    birthdate  DATE         NOT NULL COMMENT '생년월일',
    phone      VARCHAR(50)  NOT NULL COMMENT '휴대폰 번호',
    role       VARCHAR(50)  NOT NULL COMMENT '회원 권한',
    created_at DATETIME     NOT NULL COMMENT '데이터 생성일',
    updated_at DATETIME     NOT NULL COMMENT '데이터 변경일'
);

CREATE TABLE event
(
    id                     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '이벤트 식별자',
    category               VARCHAR(100) NOT NULL COMMENT '상품 카테고리',
    description            VARCHAR(255) NOT NULL COMMENT '이벤트 설명',
    start_date             DATE         NULL COMMENT '이벤트 시작일 / null일 경우 무제한 이벤트',
    end_date               DATE         NULL COMMENT '이벤트 종료일 / null일 경우 무제한 이벤트',
    daily_issue_start_time VARCHAR(20)  NULL COMMENT '당일 쿠폰 발행 시작시간 e.g. "13:00:00"/ null일 경우 무한 발행',
    daily_issue_end_time   VARCHAR(20)  NULL COMMENT '당일 쿠폰 발행 종료시간 e.g. "15:00:00" / null일 경우 무한 발행',
    created_at             DATETIME     NOT NULL COMMENT '데이터 생성일',
    updated_at             DATETIME     NOT NULL COMMENT '데이터 변경일'
);

CREATE TABLE coupon
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '쿠폰 식별자',
    event_id            BIGINT UNSIGNED NULL COMMENT '이벤트 식별자 / NULL일 경우 이벤트와 관련 없는 쿠폰(e.g. 회원가입 쿠폰)',
    discount_type       VARCHAR(50)     NOT NULL COMMENT '정액, 정률 등',
    discount_rate       BIGINT UNSIGNED NULL COMMENT '정률 할인',
    discount_price      BIGINT UNSIGNED NULL COMMENT '정액 할인',
    coupon_type         VARCHAR(100)    NOT NULL COMMENT '선착순 쿠폰, 회원가입 쿠폰 등..',
    max_quantity        BIGINT UNSIGNED NULL COMMENT '무제한 발행일 경우 NULL',
    issued_quantity     BIGINT UNSIGNED NULL COMMENT '무제한 발행일 경우 NULL',
    validate_start_date DATETIME        NOT NULL COMMENT '모든 쿠폰은 유효 시간이 있어야한다는 제약 사항 존재',
    validate_end_date   DATETIME        NOT NULL COMMENT '모든 쿠폰은 유효 시간이 있어야한다는 제약 사항 존재',
    created_at          DATETIME        NOT NULL COMMENT '데이터 생성일',
    updated_at          DATETIME        NOT NULL COMMENT '데이터 변경일'
);

CREATE TABLE coupon_issue
(
    id                            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '쿠폰 발행 기록',
    member_id                     BIGINT UNSIGNED NOT NULL COMMENT '쿠폰 ID',
    coupon_id                     BIGINT UNSIGNED NOT NULL COMMENT '회원 ID',
    coupon_status                 VARCHAR(255)    NOT NULL DEFAULT 'NOT_ACTIVE' COMMENT '유효일 전 : NOT_ACTIVE / 유효기간 : ACTIVE / 사용완료 : USED / 만료 : EXPIRED',
    created_at                    DATETIME        NOT NULL COMMENT '데이터 생성일',
    updated_at                    DATETIME        NOT NULL COMMENT '데이터 변경일',
    check_related_issued_quantity BOOLEAN                  DEFAULT FALSE COMMENT '쿠폰 발행시 발행량 체크 여부'
);

CREATE TABLE product
(
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '상품 식별자',
    category        VARCHAR(255)            NOT NULL COMMENT '상품 카테고리',
    title           VARCHAR(255)            NOT NULL COMMENT '상품명',
    description     VARCHAR(255)            NOT NULL COMMENT '상품 설명',
    original_price  DECIMAL(12, 2) UNSIGNED NOT NULL COMMENT '원래 상품 가격',
    sale_price      DECIMAL(12, 2) UNSIGNED DEFAULT 0 NOT NULL COMMENT '할인 상품 가격(쿠폰과 관계없이 전체적으로 할인할 경우)',
    min_order_price DECIMAL(12, 2) UNSIGNED NULL COMMENT '최소 주문 가격',
    created_at      DATETIME                NOT NULL COMMENT '데이터 생성일',
    updated_at      DATETIME                NOT NULL COMMENT '데이터 변경일'
);


CREATE TABLE order_detail
(
    id                   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '주문 식별자',
    product_id           BIGINT UNSIGNED         NOT NULL COMMENT '상품 ID',
    quantity             BIGINT UNSIGNED         NOT NULL COMMENT '상품 주문 수량',
    price_per_each       DECIMAL(12, 2) UNSIGNED NOT NULL COMMENT '상품 개당 가격',
    total_order_price    DECIMAL(12, 2) UNSIGNED NOT NULL COMMENT '총 상품 주문 가격',
    total_discount_price DECIMAL(12, 2) UNSIGNED NOT NULL COMMENT '총 할인 가격',
    total_payment_price  DECIMAL(12, 2) UNSIGNED NOT NULL COMMENT '총 결제 가격',
    created_at           DATETIME                NOT NULL COMMENT '데이터 생성일',
    updated_at           DATETIME                NOT NULL COMMENT '데이터 변경일'
);

## 주문 한 개에 여러 쿠폰을 사용할 수 있으므로 별도의 테이블로 분리
CREATE TABLE order_coupon
(
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '쿠폰을 사용한 주문 식별자',
    order_id        BIGINT UNSIGNED         NOT NULL COMMENT '주문 ID',
    coupon_id       BIGINT UNSIGNED         NOT NULL COMMENT '쿠폰 ID',
    discount_amount DECIMAL(12, 2) UNSIGNED NOT NULL COMMENT '쿠폰 할인액',
    created_at      DATETIME                NOT NULL COMMENT '데이터 생성일',
    updated_at      DATETIME                NOT NULL COMMENT '데이터 변경일'
);

## 인덱스 설정
ALTER TABLE coupon_issue ADD INDEX idx_member_id (member_id);

ALTER TABLE order_detail ADD INDEX idx_all (created_at, total_payment_price,total_discount_price);

ALTER TABLE order_coupon ADD INDEX idx_order_id (order_id);
