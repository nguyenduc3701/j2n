

-- 1. Lưu các con số tổng quát (Chỉ duy nhất 1 dòng)
CREATE TABLE IF NOT EXISTS global_statistics (
    id INT PRIMARY KEY DEFAULT 1,
    total_users BIGINT DEFAULT 0,
    total_rooms BIGINT DEFAULT 0,
    total_revenue_all_time DECIMAL(15, 2) DEFAULT 0.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_single_row CHECK (id = 1) -- Đảm bảo chỉ có 1 record duy nhất
);

-- Init dữ liệu ban đầu cho global_statistics
INSERT INTO global_statistics (id, total_users) VALUES (1, 1) ON DUPLICATE KEY UPDATE total_users = GREATEST(total_users, 1);
