CREATE TABLE IF NOT EXISTS bills (
    id VARCHAR(36) PRIMARY KEY, -- Sử dụng UUID từ Java để đồng bộ dễ dàng hơn
    room_id BIGINT,
    billing_month INT NOT NULL,
    electricity_old_index INT,
    electricity_new_index INT,
    electricity_usage INT,
    electricity_amount DECIMAL(19, 2) DEFAULT 0,
    water_amount DECIMAL(19, 2) DEFAULT 0,
    service_amount DECIMAL(19, 2) DEFAULT 0,
    total_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'UNPAID', -- UNPAID, PAID, PARTIAL, CANCELLED
    order_id BIGINT, -- Link tới order-srv sau khi tạo lệnh thanh toán
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);
