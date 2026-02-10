-- Tạo bảng configuration
CREATE TABLE IF NOT EXISTS configuration (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(255) NOT NULL,    -- Tên biến
    config_value TEXT,                   -- Giá trị của biến
    service_id VARCHAR(50) NOT NULL, 
    environment VARCHAR(50) NOT NULL,     -- Môi trường (dev, prod, test)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
