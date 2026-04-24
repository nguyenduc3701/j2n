CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID',
    user_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'VND',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, SUCCESS, FAILED, CANCELLED',
    payment_method VARCHAR(50) NOT NULL COMMENT 'PAYOS, MANUAL_QR',
    external_tx_id VARCHAR(255) COMMENT 'PayOS order code or bank reference',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_external_tx_id (external_tx_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
