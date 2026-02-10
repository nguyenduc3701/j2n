-- Tạo bảng service
CREATE TABLE IF NOT EXISTS service (
    id VARCHAR(50) NOT NULL PRIMARY KEY,  -- ID của service (ví dụ: auth-srv, user-srv)
    name VARCHAR(255) NOT NULL           -- Tên hiển thị của service
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
