-- Insert Fees
INSERT INTO fees (name, unit_price, unit_name) VALUES
('Water', 100000, 'Person'),
('Electricity', 4000, 'Unit'),
('Internet', 50000, 'Room'),
('Washing machine', 100000, 'Person'),
('Electric vehicle', 100000, 'Vehicle');

-- Insert Rooms (Floor 3 to 5, each floor has "Inside" and "Outside")
INSERT INTO rooms (room_number, floor, base_price, area, max_people, status, description) VALUES
('P301', 3, 2500000, '20m2', 2, 'AVAILABLE', 'Floor 3 Inside'),
('P302', 3, 2500000, '20m2', 2, 'AVAILABLE', 'Floor 3 Outside'),
('P401', 4, 2500000, '20m2', 2, 'AVAILABLE', 'Floor 4 Inside'),
('P402', 4, 2500000, '20m2', 2, 'AVAILABLE', 'Floor 4 Outside'),
('P501', 5, 2500000, '20m2', 2, 'AVAILABLE', 'Floor 5 Inside'),
('P502', 5, 2500000, '20m2', 2, 'AVAILABLE', 'Floor 5 Outside');

-- Insert Master Assets
INSERT INTO assets (name) VALUES
('Water heater'),
('Air conditioner'),
('Wardrobe'),
('Refrigerator'),
('Pallet bed'),
('Mattress'),
('Dressing table'),
('Shower set'),
('Washbasin'),
('Toilet');

-- Insert Room Assets (Linking rooms with master assets)
INSERT INTO room_assets (room_id, asset_id)
SELECT r.id, a.id
FROM rooms r
CROSS JOIN assets a;

-- Insert Room Fees (Linking every room with basic fees: Water, Electricity, Internet)
INSERT INTO room_fees (room_id, fee_id)
SELECT r.id, f.id
FROM rooms r
CROSS JOIN fees f
WHERE f.name IN ('Water', 'Electricity', 'Internet');
