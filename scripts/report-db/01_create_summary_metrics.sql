-- 1. Các con số tổng quát (Dùng cho Top Cards & Cảnh báo)
-- Lưu: Tổng User, Sản phẩm còn lại, Tổng Package, Phòng trống...
CREATE TABLE IF NOT EXISTS summary_metrics (
    metric_key VARCHAR(100) PRIMARY KEY, -- 'total_users', 'empty_rooms', 'store_remaining_products', 'total_packages'
    category VARCHAR(50),                -- 'ACCOUNT', 'ROOM', 'STORE', 'PRODUCT'
    metric_value BIGINT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

