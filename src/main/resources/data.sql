
## MEMBER
INSERT INTO member (email, password, name, birthdate, phone, created_at, updated_at,role)
VALUES('sejin@email.com','$2a$10$o/LuVqMoJZlRgPKUHyLJWegiwmPOjtVYoyH9uKu.nkQ/0Rpk52ew.','박세진','1111-11-12','010-222-222','2023-12-12','2023-12-12', 'ROLE_USER');

## COUPON
INSERT INTO coupon (id, event_id, discount_type, discount_rate, discount_price, coupon_type, max_quantity,
                    issued_quantity, validate_start_date, validate_end_date, created_at, updated_at)
VALUES (1,1,'PERCENT',50,null,'FIRST_COME_FIRST_SERVED',500,0,'2024-02-01','2024-02-05','2024-02-01','2024-02-01');

## EVENT
INSERT INTO event (id, category, description, start_date, end_date, daily_issue_start_time,
                              daily_issue_end_time, created_at, updated_at)
VALUES (1, '바디케어' , '바디케어 전품목 이벤트', '2024-02-27','2024-02-29','13:00:00','15:00:00','2024-02-01','2024-02-01');

SELECT max_quantity, issued_quantity
FROM coupon;

SELECT count(*)
FROM coupon_issue;
