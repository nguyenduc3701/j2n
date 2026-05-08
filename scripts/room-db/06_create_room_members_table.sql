CREATE TABLE IF NOT EXISTS room_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL, -- User ID from auth-srv
    is_primary BOOLEAN DEFAULT FALSE, -- Mark who is responsible for payment
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);
