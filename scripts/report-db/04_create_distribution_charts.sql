-- 4. Dữ liệu phân bổ (Dùng cho tất cả Pie Charts)
-- Lưu: Phân loại User, Sản phẩm bán chạy %, Package phổ biến %
CREATE TABLE IF NOT EXISTS distribution_charts (
    chart_type VARCHAR(50),  -- 'USER_TYPE', 'POPULAR_PRODUCT', 'POPULAR_PACKAGE'
    item_label VARCHAR(100), -- 'Renter', 'Sản phẩm A', 'Product Đà Lạt'
    item_value BIGINT DEFAULT 0,
    PRIMARY KEY (chart_type, item_label)
);

-- Init user distribution with 1 Admin
INSERT INTO distribution_charts (chart_type, item_label, item_value) 
VALUES ('USER_TYPE', 'ADMIN', 1) 
ON DUPLICATE KEY UPDATE item_value = GREATEST(item_value, 1);
