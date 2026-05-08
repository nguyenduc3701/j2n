CREATE TABLE product_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    title VARCHAR(255),
    content TEXT,
    hotel VARCHAR(255),
    breakfast VARCHAR(255) DEFAULT NULL,
    lunch VARCHAR(255) DEFAULT NULL,
    dinner VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    INDEX idx_product_id (product_id),
    INDEX idx_is_deleted (is_deleted),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
