-- Insert records into service table
INSERT INTO service (id, name) VALUES
('api-gateway-srv', 'API Gateway'),
('auth-srv', 'Auth Service'),
('bff-srv', 'BFF Service'),
('config-srv', 'Configuration Service'),
('image-srv', 'Image Service'),
('report-srv', 'Report Service')
ON DUPLICATE KEY UPDATE name = VALUES(name);
