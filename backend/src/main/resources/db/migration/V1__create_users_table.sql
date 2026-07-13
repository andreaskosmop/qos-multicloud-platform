CREATE TABLE users (
    id                   VARCHAR(255) PRIMARY KEY,
    username             VARCHAR(50)  UNIQUE NOT NULL,
    full_name            VARCHAR(100) NOT NULL,
    email                VARCHAR(255) UNIQUE NOT NULL,
    password_hash        VARCHAR(255) NOT NULL,
    role                 VARCHAR(20) NOT NULL DEFAULT 'REGULAR',  -- ENUM: REGULAR/ADMIN
    is_active            BOOLEAN NOT NULL DEFAULT FALSE,
    security_question    TEXT,
    security_answer_hash VARCHAR(255),
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP
);
