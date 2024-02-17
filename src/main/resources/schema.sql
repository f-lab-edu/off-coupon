DROP TABLE IF EXISTS member;

CREATE TABLE member
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '회원 식별자',
    email VARCHAR(40) NOT NULL COMMENT '회원 이메일',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호',
    name VARCHAR(40) NOT NULL COMMENT '회원 이름',
    birthdate DATETIME NOT NULL COMMENT '생년월일',
    phone VARCHAR(40) NOT NULL COMMENT '휴대폰 번호',
    role VARCHAR(50) NOT NULL COMMENT '회원 권한',
    created_at DATETIME NOT NULL COMMENT '데이터 생성일',
    updated_at DATETIME NOT NULL COMMENT '데이터 변경일'
);


DROP TABLE IF EXISTS datedemo;

CREATE TABLE datedemo
(
    my_date_time DATETIME NOT NULL COMMENT '데이터타임',
    my_time_stamp TIMESTAMP NOT NULL COMMENT '타임스탬프'
);
