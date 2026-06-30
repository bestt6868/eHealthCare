DECLARE @matKhau NVARCHAR(255) = N'Admin@1234';

INSERT INTO NHAN_VIEN_QUAN_LY (hoTenNhanVien, matKhauHash)
VALUES (
    N'Odegard Martin',
    LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', @matKhau), 2))
);

-- Lấy mã nhân viên vừa tạo (IDENTITY tự sinh)
SELECT SCOPE_IDENTITY() AS maNhanVienMoi;