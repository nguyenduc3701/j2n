CREATE TABLE IF NOT EXISTS room_utilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT,
    utility_config_id BIGINT,
    quantity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (utility_config_id) REFERENCES utility_configs(id)
);
