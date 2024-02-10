DROP TABLE IF EXISTS member;

CREATE TABLE member
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '회원 식별자',
    email VARCHAR(40) NOT NULL COMMENT '회원 이메일',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호',
    name VARCHAR(40) NOT NULL COMMENT '회원 이름',
    birthdate VARCHAR(40) NOT NULL COMMENT '생년월일',
    phone VARCHAR(40) NOT NULL COMMENT '휴대폰 번호',
    created_at TIMESTAMP NOT NULL COMMENT '데이터 생성일',
    updated_at TIMESTAMP NOT NULL COMMENT '데이터 변경일'
);
