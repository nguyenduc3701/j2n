CREATE TABLE tours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(15, 2) NOT NULL,
    thumbnail VARCHAR(255),
    duration VARCHAR(100),
    start_location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    INDEX idx_category_id (category_id),
    INDEX idx_is_deleted (is_deleted),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
