CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    INDEX idx_slug (slug),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_type (type)
);
