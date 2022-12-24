DROP TABLE IF EXISTS employee;
CREATE TABLE employee (
                      origin_id CHAR(36) NOT NULL PRIMARY KEY,
                      username VARCHAR(255) NULL,
                      company_name VARCHAR(255) NULL,
                      role VARCHAR(255) NOT NULL,
);