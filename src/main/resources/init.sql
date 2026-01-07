-- =====================================================
-- Database: ql_kho_thuoc (Quản lý kho thuốc)
-- =====================================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS ql_kho_thuoc 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE ql_kho_thuoc;

-- =====================================================
-- 1. Bảng: category (Loại thuốc)
-- =====================================================
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. Bảng: medicine (Danh sách thuốc)
-- =====================================================
CREATE TABLE medicine (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    image VARCHAR(255),
    price DECIMAL(12,2) NOT NULL,
    brand VARCHAR(100),
    description TEXT,
    expiry_date DATE NOT NULL,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id),
    INDEX idx_name (name),
    INDEX idx_expiry (expiry_date),
    INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. Bảng: supplier (Nhà cung cấp)
-- =====================================================
CREATE TABLE supplier (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. Bảng: stock_in (Nhập kho)
-- =====================================================
CREATE TABLE stock_in (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    supplier_id INT,
    quantity INT NOT NULL,
    date_in DATE NOT NULL,
    FOREIGN KEY (medicine_id) REFERENCES medicine(id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    INDEX idx_medicine (medicine_id),
    INDEX idx_date (date_in)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. Bảng: stock_out (Xuất kho)
-- =====================================================
CREATE TABLE stock_out (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    quantity INT NOT NULL,
    date_out DATE NOT NULL,
    FOREIGN KEY (medicine_id) REFERENCES medicine(id),
    INDEX idx_medicine (medicine_id),
    INDEX idx_date (date_out)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. View: view_inventory (Tồn kho)
-- =====================================================
CREATE VIEW view_inventory AS
SELECT 
    m.id,
    m.name,
    IFNULL(SUM(si.quantity),0) - IFNULL(SUM(so.quantity),0) AS stock_quantity
FROM medicine m
LEFT JOIN stock_in si ON m.id = si.medicine_id
LEFT JOIN stock_out so ON m.id = so.medicine_id
GROUP BY m.id, m.name;

-- =====================================================
-- 7. View: view_expiring_soon (Thuốc sắp hết hạn)
-- =====================================================
CREATE VIEW view_expiring_soon AS
SELECT *
FROM medicine
WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY);

-- =====================================================
-- 8. View: view_daily_revenue (Doanh thu theo ngày)
-- =====================================================
CREATE VIEW view_daily_revenue AS
SELECT 
    so.date_out,
    SUM(so.quantity * m.price) AS revenue
FROM stock_out so
JOIN medicine m ON so.medicine_id = m.id
GROUP BY so.date_out;

-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

-- Loại thuốc
INSERT INTO category(name) VALUES
('Giảm đau'),
('Kháng sinh'),
('Vitamin'),
('Tiêu hóa');

-- Nhà cung cấp
INSERT INTO supplier(name, phone, address) VALUES
('Công ty Dược A', '0123456789', '123 Nguyễn Huệ, TP.HCM'),
('Công ty Dược B', '0987654321', '456 Trần Hưng Đạo, Hà Nội'),
('Công ty Dược C', '0965432123', '789 Lê Lợi, Đà Nẵng');

-- Thuốc
INSERT INTO medicine(name, price, brand, expiry_date, category_id, description)
VALUES
('Paracetamol 500mg', 5000, 'DHG', '2026-12-31', 1, 'Thuốc giảm đau, hạ sốt'),
('Amoxicillin 250mg', 12000, 'Traphaco', '2025-10-15', 2, 'Kháng sinh phổ rộng'),
('Vitamin C 1000mg', 8000, 'Herbal', '2027-06-20', 3, 'Tăng miễn dịch'),
('Omeprazole 20mg', 6500, 'Cipla', '2026-08-30', 4, 'Điều trị viêm dạ dày'),
('Aspirin 100mg', 4500, 'Bayer', '2026-11-15', 1, 'Giảm đau, chống cách máu'),
('Cetirizine 10mg', 7000, 'Remedica', '2026-09-10', 1, 'Thuốc kháng dị ứng');

-- Nhập kho
INSERT INTO stock_in(medicine_id, supplier_id, quantity, date_in)
VALUES 
(1, 1, 100, '2024-01-15'),
(2, 2, 80, '2024-01-20'),
(3, 3, 120, '2024-02-10'),
(4, 1, 50, '2024-02-15'),
(5, 2, 150, '2024-02-20'),
(6, 3, 90, '2024-03-01');

-- Xuất kho
INSERT INTO stock_out(medicine_id, quantity, date_out)
VALUES 
(1, 10, '2024-03-01'),
(1, 5, '2024-03-05'),
(2, 8, '2024-03-02'),
(3, 15, '2024-03-03'),
(4, 3, '2024-03-04'),
(5, 20, '2024-03-05'),
(6, 12, '2024-03-06');

-- =====================================================
-- KẾT THÚC TẠO CƠ SỞ DỮ LIỆU
-- =====================================================
