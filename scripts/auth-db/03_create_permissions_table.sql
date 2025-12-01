CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE
);

-- Tạo index cho is_deleted nếu cần filter soft-deleted permissions
CREATE INDEX idx_permissions_is_deleted ON permissions(is_deleted);
