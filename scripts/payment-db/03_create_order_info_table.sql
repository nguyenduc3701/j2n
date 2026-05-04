CREATE TABLE IF NOT EXISTS order_info (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(36)  NOT NULL,
    item_id   VARCHAR(255) NOT NULL,
    item_type VARCHAR(100) NOT NULL,
    quantity  INT          NOT NULL DEFAULT 1,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_item (user_id, item_id, item_type),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
