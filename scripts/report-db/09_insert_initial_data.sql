-- Init summary metrics
INSERT INTO summary_metrics (metric_key, category, metric_value) VALUES
('active_users', 'ACCOUNT', 1),
('total_rooms', 'ROOM', 6),
('empty_rooms', 'ROOM', 6),
('paid_rooms', 'ROOM', 0),
('total_bills_this_month', 'FINANCE', 0),
('remaining_unpaid_amount_this_month', 'FINANCE', 0),
('total_electricity_amount_this_month', 'UTILITY', 0),
('total_water_amount_this_month', 'UTILITY', 0)
ON DUPLICATE KEY UPDATE metric_value = VALUES(metric_value);