-- ============================================================
-- HospitalKioskDB - Tạo toàn bộ schema cơ sở dữ liệu
-- Tác giả: HospitalKiosk System
-- ============================================================

CREATE DATABASE HospitalKioskDB;
GO
USE HospitalKioskDB;
GO

-- Bảng BENH_NHAN: lưu thông tin bệnh nhân
CREATE TABLE BENH_NHAN (
    maBaoHiemYTe  VARCHAR(15)   NOT NULL PRIMARY KEY,  -- BHYT 15 ký tự
    hoTen         NVARCHAR(255) NOT NULL,
    gioiTinh      NVARCHAR(50)  NOT NULL,
    ngaySinh      DATE          NOT NULL,
    trangThaiThe  NVARCHAR(100) NOT NULL               -- 'Con hieu luc' hoac 'Het han'
);
GO

-- Bảng NHAN_VIEN_QUAN_LY: lưu tài khoản nhân viên quản trị
CREATE TABLE NHAN_VIEN_QUAN_LY (
    maNhanVien    INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    hoTenNhanVien NVARCHAR(255) NOT NULL,
    matKhauHash   VARCHAR(255)  NOT NULL               -- mật khẩu đã hash SHA-256
);
GO

-- Bảng CHUYEN_KHOA: danh sách chuyên khoa bệnh viện
CREATE TABLE CHUYEN_KHOA (
    maKhoa        INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    tenKhoa       NVARCHAR(255) NOT NULL UNIQUE,
    trangThai     BIT           NOT NULL DEFAULT 1,    -- 1=hoat dong, 0=tat
    maNhanVien    INT           NOT NULL,
    FOREIGN KEY (maNhanVien) REFERENCES NHAN_VIEN_QUAN_LY(maNhanVien)
);
GO

-- Bảng BAC_SI: danh sách bác sĩ
CREATE TABLE BAC_SI (
    maBacSi       INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    tenBacSi      NVARCHAR(255) NOT NULL,
    maKhoa        INT           NOT NULL,
    trangThai     BIT           NOT NULL DEFAULT 1,    -- 1=hoat dong, 0=tat
    maNhanVien    INT           NOT NULL,
    FOREIGN KEY (maKhoa)     REFERENCES CHUYEN_KHOA(maKhoa),
    FOREIGN KEY (maNhanVien) REFERENCES NHAN_VIEN_QUAN_LY(maNhanVien)
);
GO

-- Bảng HANG_CHO: hàng chờ theo bác sĩ theo ngày
CREATE TABLE HANG_CHO (
    maHangCho     INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    soNguoiCho    INT           NOT NULL DEFAULT 0,
    ngay          DATE          NOT NULL,
    maBacSi       INT           NOT NULL,
    maNhanVien    INT           NOT NULL,
    FOREIGN KEY (maBacSi)    REFERENCES BAC_SI(maBacSi),
    FOREIGN KEY (maNhanVien) REFERENCES NHAN_VIEN_QUAN_LY(maNhanVien),
    UNIQUE (maBacSi, ngay)                             -- mỗi bác sĩ chỉ 1 hàng chờ/ngày
);
GO

-- Bảng PHIEU_KHAM: phiếu khám đã cấp cho bệnh nhân
CREATE TABLE PHIEU_KHAM (
    maPhieuKham     INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    ngayKham        DATE          NOT NULL,
    soThuTu         INT           NOT NULL,
    maHangCho       INT           NOT NULL,
    maBaoHiemYTe    VARCHAR(15)   NOT NULL,
    ngayGioLayPhieu DATETIME      NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (maHangCho)    REFERENCES HANG_CHO(maHangCho),
    FOREIGN KEY (maBaoHiemYTe) REFERENCES BENH_NHAN(maBaoHiemYTe)
);
GO

-- Bảng CA_TRUC: thông tin ca trực
CREATE TABLE CA_TRUC (
    maCaTruc      INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    tenCaTruc     NVARCHAR(255) NOT NULL,
    gioBatDau     TIME          NOT NULL,
    gioKetThuc    TIME          NOT NULL,
    ngayTruc      DATE          NOT NULL,
    maNhanVien    INT           NOT NULL,
    FOREIGN KEY (maNhanVien) REFERENCES NHAN_VIEN_QUAN_LY(maNhanVien)
);
GO

-- Bảng LICH_CA_TRUC: bảng trung gian N-N giữa BAC_SI và CA_TRUC
CREATE TABLE LICH_CA_TRUC (
    maCaTruc      INT NOT NULL,
    maBacSi       INT NOT NULL,
    PRIMARY KEY (maCaTruc, maBacSi),
    FOREIGN KEY (maCaTruc) REFERENCES CA_TRUC(maCaTruc),
    FOREIGN KEY (maBacSi)  REFERENCES BAC_SI(maBacSi)
);
GO
