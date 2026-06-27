-- ============================================================
-- HospitalKioskDB - Dữ liệu mẫu để kiểm thử
-- ============================================================
USE HospitalKioskDB;
GO

-- 1 nhân viên quản lý (mật khẩu: Admin@123, đã hash SHA-256)
INSERT INTO NHAN_VIEN_QUAN_LY (hoTenNhanVien, matKhauHash)
VALUES (N'Nguyễn Văn Admin',
        'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7');
GO

-- 3 khoa
INSERT INTO CHUYEN_KHOA (tenKhoa, trangThai, maNhanVien) VALUES
(N'Nội tổng quát', 1, 1),
(N'Nhi khoa',      1, 1),
(N'Tai Mũi Họng',  1, 1);
GO

-- 5 bác sĩ
INSERT INTO BAC_SI (tenBacSi, maKhoa, trangThai, maNhanVien) VALUES
(N'Bs. Trần Văn An',  1, 1, 1),
(N'Bs. Lê Thị Bình', 1, 1, 1),
(N'Bs. Phạm Hùng',   2, 1, 1),
(N'Bs. Nguyễn Mai',  2, 1, 1),
(N'Bs. Hoàng Sơn',   3, 1, 1);
GO

-- Bệnh nhân mẫu
INSERT INTO BENH_NHAN (maBaoHiemYTe, hoTen, gioiTinh, ngaySinh, trangThaiThe) VALUES
('DN123456789012345', N'Nguyễn Thị Lan', N'Nữ',  '1985-03-15', N'Con hieu luc'),
('HN987654321098765', N'Trần Văn Bảo',   N'Nam', '1972-07-22', N'Con hieu luc'),
('SG111222333444555', N'Lê Minh Châu',   N'Nam', '1990-11-05', N'Het han');
GO

PRINT N'Dữ liệu mẫu đã được chèn thành công.';
PRINT N'Tài khoản đăng nhập:';
PRINT N'  Mã nhân viên: 1';
PRINT N'  Mật khẩu: Admin@123';
GO
