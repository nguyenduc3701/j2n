-- 5. Chương trình giảm giá (Discount Program)
CREATE TABLE IF NOT EXISTS active_promotions (
    promo_id VARCHAR(50) PRIMARY KEY,
    domain VARCHAR(20),      -- 'STORE', 'TRAVEL'
    promo_name VARCHAR(255),
    description VARCHAR(255),
    end_date DATE
);
