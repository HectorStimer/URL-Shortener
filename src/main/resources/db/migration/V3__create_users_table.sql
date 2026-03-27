CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY UNIQUE NOT NULL ,
                       email VARCHAR(50) NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) CHECK (role IN ('ADMIN', 'USER'))
);