-- ============================================================
-- HospitalKioskDB - Stored Procedures
-- ============================================================
USE HospitalKioskDB;
GO

-- SP: Lấy hoặc tạo hàng chờ cho bác sĩ theo ngày
CREATE OR ALTER PROCEDURE sp_LayHoatDongHangCho
    @maBacSi   INT,
    @ngay      DATE,
    @maNhanVien INT
AS
BEGIN
    SET NOCOUNT ON;
    -- Nếu chưa có hàng chờ thì tạo mới
    IF NOT EXISTS (SELECT 1 FROM HANG_CHO WHERE maBacSi = @maBacSi AND ngay = @ngay)
    BEGIN
        INSERT INTO HANG_CHO (soNguoiCho, ngay, maBacSi, maNhanVien)
        VALUES (0, @ngay, @maBacSi, @maNhanVien);
    END
    SELECT * FROM HANG_CHO WHERE maBacSi = @maBacSi AND ngay = @ngay;
END;
GO

-- SP: Tăng số người chờ và trả về số thứ tự mới
CREATE OR ALTER PROCEDURE sp_TangSoNguoiCho
    @maHangCho INT,
    @soThuTuMoi INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE HANG_CHO SET soNguoiCho = soNguoiCho + 1 WHERE maHangCho = @maHangCho;
    SELECT @soThuTuMoi = soNguoiCho FROM HANG_CHO WHERE maHangCho = @maHangCho;
END;
GO

PRINT N'Stored procedures đã được tạo thành công.';
GO
