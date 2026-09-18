-- V15__add_staff_code_to_staff.sql
ALTER TABLE staff ADD COLUMN IF NOT EXISTS staff_code VARCHAR(50);

CREATE INDEX IF NOT EXISTS idx_staff_code ON staff(staff_code);

-- Populate numeric digit-only staff_code for existing staff members
UPDATE staff SET staff_code = '1001' WHERE (staff_code IS NULL OR staff_code = '' OR staff_code LIKE 'EMP%') AND role = 'ADMIN';
UPDATE staff SET staff_code = '1002' WHERE (staff_code IS NULL OR staff_code = '' OR staff_code LIKE 'EMP%') AND role = 'CASHIER';
UPDATE staff SET staff_code = '1003' WHERE staff_code IS NULL OR staff_code = '' OR staff_code LIKE 'EMP%';
