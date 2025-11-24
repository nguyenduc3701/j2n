INSERT IGNORE INTO roles (name, description)
VALUES
    ('ADMIN', 'Full system access'),
    ('RECRUITER', 'Access to most of the system features'),
    ('RENTER', 'Access to room-only features'),
    ('VISITER', 'Access to basic public pages');
