
SELECT id,
       email,
       password,
       name,
       birthdate,
       phone,
       role,
       created_at,
       updated_at
FROM  member
WHERE email = 'sejin@email.com10';

INSERT INTO member (email, password, name, birthdate, phone, created_at, updated_at,role)
VALUES('sejin@email.com','$2a$10$o/LuVqMoJZlRgPKUHyLJWegiwmPOjtVYoyH9uKu.nkQ/0Rpk52ew.','박세진','1111-11-12','010-222-222','2023-12-12','2023-12-12', 'ROLE_USER');