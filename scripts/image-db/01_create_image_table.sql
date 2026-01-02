CREATE TABLE images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_type VARCHAR(50) NOT NULL,
    owner_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    image_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_owner (owner_type, owner_id),
    INDEX idx_type (image_type)
);

CREATE INDEX idx_images_is_deleted ON images(is_deleted);

