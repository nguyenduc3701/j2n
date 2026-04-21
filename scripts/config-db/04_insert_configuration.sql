-- Insert configuration for auth.token.expired-time-second
INSERT INTO configuration (config_key, config_value, service_id, environment) VALUES
-- Application Configuration
('application.rabbitmq.host', 'localhost', 'application', 'dev'),
('application.rabbitmq.port', '9192', 'application', 'dev'),
('application.rabbitmq.username', 'guest', 'application', 'dev'),
('application.rabbitmq.password', 'guest', 'application', 'dev'),
('application.database.base-url', 'jdbc:mysql://localhost:3306', 'application', 'dev'),
('application.database.username', 'root', 'application', 'dev'),
('application.database.password', 'Welcome1', 'application', 'dev'),
('application.redis.host', 'localhost', 'application', 'dev'),
('application.redis.port', '9194', 'application', 'dev'),
('application.redis.password', 'yourpassword123', 'application', 'dev'),
('application.redis.timeout', '10000', 'application', 'dev'),
('application.page.default-size', '10', 'application', 'dev'),
('application.jwt.expiration-ms', '14400000', 'application', 'dev'),
('application.jwt.clock-skew-seconds', '60', 'application', 'dev'),
('application.multipart.max-file-size', '50MB', 'application', 'dev'),
('application.multipart.max-request-size', '50MB', 'application', 'dev'),
('application.access-token.expired-time-seconds', '14400', 'application', 'dev'),
('application.refresh-token.expired-time-days', '7', 'application', 'dev'),
('application.api-gateway.base-url', 'http://localhost:8181', 'application', 'dev'),

-- Config Server Configuration
('application.config.base-url', 'http://localhost:8185', 'application', 'dev'),
-- Api Gateway Service Configuration
('api-gateway.max-in-memory-size', '50MB', 'api-gateway-srv', 'dev'),
('api-gateway.auth.base-url', 'http://localhost:8182', 'api-gateway-srv', 'dev'),
('api-gateway.report.base-url', 'http://localhost:18184', 'api-gateway-srv', 'dev'),
('api-gateway.report.discovery.retry-ms', '60000', 'api-gateway-srv', 'dev'),
('api-gateway.image.base-url', 'http://localhost:8183', 'api-gateway-srv', 'dev'),
('api-gateway.bff.base-url', 'http://localhost:8180', 'api-gateway-srv', 'dev'),
('api-gateway.travel.base-url', 'http://localhost:8186', 'api-gateway-srv', 'dev'),
('api-gateway.payment.base-url', 'http://localhost:8187', 'api-gateway-srv', 'dev'),
('api-gateway.order.base-url', 'http://localhost:8188', 'api-gateway-srv', 'dev'),

-- Auth Service Configuration
-- Report Service Configuration
('report.cache.time-minutes', '1', 'report-srv', 'dev'),
-- Image Service Configuration
('image.minio.url', 'http://localhost:9190', 'image-srv', 'dev'),
('image.minio.access-key', 'minio', 'image-srv', 'dev'),
('image.minio.secret-key', 'minio123', 'image-srv', 'dev'),
-- BFF Service Configuration
('bff.base-url', 'http://localhost:8180', 'bff-srv', 'dev'),
-- Frontend Configuration
('travel-portal.base-url', 'http://localhost:3104', 'frontend', 'dev'),
('store-portal.base-url', 'http://localhost:3103', 'frontend', 'dev'),
('room-portal.base-url', 'http://localhost:3102', 'frontend', 'dev'),
('management-portal.base-url', 'http://localhost:3101', 'frontend', 'dev'),
('about-portal.base-url', 'http://localhost:3100', 'frontend', 'dev');
