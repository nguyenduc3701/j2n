-- 3. Chỉ số Tiện ích (Riêng cho mảng Room)
-- Lưu: Tổng điện, Tổng nước tiêu thụ toàn hệ thống theo tháng
CREATE TABLE IF NOT EXISTS room_utility_reports (
    month_year VARCHAR(7) PRIMARY KEY,
    total_electricity DECIMAL(15, 2) DEFAULT 0.00,
    total_water DECIMAL(15, 2) DEFAULT 0.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
