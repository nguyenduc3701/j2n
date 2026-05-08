CREATE TABLE IF NOT EXISTS room_bill_info (
    id VARCHAR(36) PRIMARY KEY, -- bill_id từ room-srv
    room_number VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL, -- Ví dụ: "Tiền phòng tháng 05/2026 - P.101"
    total_amount DECIMAL(19, 2) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
