

-- 2. Lưu số lượng theo loại User (Dùng cho Pie Chart)
CREATE TABLE IF NOT EXISTS user_distribution (
    user_type VARCHAR(50) PRIMARY KEY, -- VISITOR, RECRUITER, RENTER
    count BIGINT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Init dữ liệu cho Admin
INSERT INTO user_distribution (user_type, count) VALUES ('ADMIN', 1) ON DUPLICATE KEY UPDATE count = GREATEST(count, 1);
