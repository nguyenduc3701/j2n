-- Insert Fees
INSERT INTO fees (name, unit_price, unit_name) VALUES
('Nước', 100000, 'Người'),
('Điện', 4000, 'Số'),
('Mạng', 50000, 'Phòng'),
('Máy giặt', 100000, 'Người'),
('Xe điện', 100000, 'Xe');

-- Insert Rooms (Floor 3 to 5, each floor has "Trong" and "Ngoài")
INSERT INTO rooms (room_number, floor, base_price, area, max_people, status, description) VALUES
('P301', 3, 2500000, '20m2', 2, 'OCCUPIED', 'Tầng 3 Trong'),
('P302', 3, 2500000, '20m2', 2, 'OCCUPIED', 'Tầng 3 Ngoài'),
('P401', 4, 2500000, '20m2', 2, 'OCCUPIED', 'Tầng 4 Trong'),
('P402', 4, 2500000, '20m2', 2, 'OCCUPIED', 'Tầng 4 Ngoài'),
('P501', 5, 2500000, '20m2', 2, 'OCCUPIED', 'Tầng 5 Trong'),
('P502', 5, 2500000, '20m2', 2, 'OCCUPIED', 'Tầng 5 Ngoài');

-- Insert Master Assets
INSERT INTO assets (name) VALUES
('Nóng lạnh'),
('Điều hoà'),
('Tủ quần áo'),
('Tủ lạnh'),
('Giường Pallet'),
('Đệm'),
('Bàn trang điểm'),
('Bộ vòi sen tắm'),
('Bồn rửa mặt'),
('Bồn cầu vệ sinh');

-- Insert Room Assets (Linking rooms with master assets)
INSERT INTO room_assets (room_id, object_id)
SELECT r.id, a.id
FROM rooms r
CROSS JOIN assets a;

-- Insert Room Fees (Linking every room with basic fees: Nước, Điện, Mạng)
INSERT INTO room_fees (room_id, fee_id)
SELECT r.id, f.id
FROM rooms r
CROSS JOIN fees f
WHERE f.name IN ('Nước', 'Điện', 'Mạng');
