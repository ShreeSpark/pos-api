-- V17__ensure_cashier_and_staff_codes.sql

-- Ensure all ADMIN staff have staff_code '1001' if missing
UPDATE staff SET staff_code = '1001' WHERE (staff_code IS NULL OR staff_code = '') AND role = 'ADMIN';

-- Ensure all CASHIER staff have staff_code '1002' if missing
UPDATE staff SET staff_code = '1002' WHERE (staff_code IS NULL OR staff_code = '') AND role = 'CASHIER';
