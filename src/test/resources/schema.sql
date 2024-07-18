
CREATE TABLE IF NOT EXISTS "USER" (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255),
    mobile_number VARCHAR(20),
    email_id VARCHAR(100),
    password VARBINARY(255) NOT NULL,
    password_status VARCHAR(50),
    session VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS transaction (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount BIGINT,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES "USER"(user_id)
);
