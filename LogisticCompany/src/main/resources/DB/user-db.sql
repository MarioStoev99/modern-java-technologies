DROP TABLE IF EXISTS user_db;

CREATE TABLE user_db
(
    origin_id CHAR(36)     NOT NULL PRIMARY KEY,
    username  VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(255) NOT NULL,
    logged_in VARCHAR(255),
);