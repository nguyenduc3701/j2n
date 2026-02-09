

-- 3. Lưu số liệu theo tháng (Dùng cho Line/Bar Chart)
CREATE TABLE IF NOT EXISTS monthly_metrics (
    month_year VARCHAR(7) PRIMARY KEY, -- Định dạng '2026-02'
    new_users INT DEFAULT 0,
    monthly_revenue DECIMAL(15, 2) DEFAULT 0.00,
    paid_amount DECIMAL(15, 2) DEFAULT 0.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
