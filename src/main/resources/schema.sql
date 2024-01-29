DROP TABLE IF EXISTS memeber;

CREATE TABLE member
(
    id IDENTITY PRIMARY KEY,
    email VARCHAR(40) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(40) NOT NULL,
    birthDate VARCHAR(40) NOT NULL,
    phone VARCHAR(40) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);