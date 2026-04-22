-- 2. Tài chính và Đơn hàng theo tháng (Dùng cho tất cả BarCharts)
-- Lưu: Doanh thu Room, Store, Travel; Số tiền còn nợ; Tổng đơn hàng...
CREATE TABLE IF NOT EXISTS monthly_financials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    month_year VARCHAR(7),      -- Định dạng 'YYYY-MM'
    domain VARCHAR(20),         -- 'ROOM', 'STORE', 'PRODUCT'
    total_income DECIMAL(15, 2) DEFAULT 0.00,
    total_orders INT DEFAULT 0,
    remaining_amount DECIMAL(15, 2) DEFAULT 0.00, -- Chỉ số 'Room bill total amount remaining'
    UNIQUE KEY idx_month_domain (month_year, domain)
);
