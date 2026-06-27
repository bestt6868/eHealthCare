package model;

import java.time.LocalDate;

/**
 * Entity đại diện cho hàng chờ khám bệnh của một bác sĩ trong một ngày.
 * Ánh xạ với bảng HANG_CHO trong cơ sở dữ liệu.
 */
public class HangCho {

    private int       maHangCho;
    private int       soNguoiCho;
    private LocalDate ngay;
    private int       maBacSi;
    private int       maNhanVien;

    // Trường hiển thị từ JOIN
    private String tenBacSi;
    private String tenKhoa;

    /** Constructor mặc định */
    public HangCho() {}

    /** Constructor đầy đủ tham số */
    public HangCho(int maHangCho, int soNguoiCho, LocalDate ngay,
                   int maBacSi, int maNhanVien) {
        this.maHangCho   = maHangCho;
        this.soNguoiCho  = soNguoiCho;
        this.ngay        = ngay;
        this.maBacSi     = maBacSi;
        this.maNhanVien  = maNhanVien;
    }

    public int  getMaHangCho()                { return maHangCho; }
    public void setMaHangCho(int maHangCho)   { this.maHangCho = maHangCho; }

    public int  getSoNguoiCho()                { return soNguoiCho; }
    public void setSoNguoiCho(int soNguoiCho)  { this.soNguoiCho = soNguoiCho; }

    public LocalDate getNgay()               { return ngay; }
    public void      setNgay(LocalDate ngay) { this.ngay = ngay; }

    public int  getMaBacSi()             { return maBacSi; }
    public void setMaBacSi(int maBacSi)  { this.maBacSi = maBacSi; }

    public int  getMaNhanVien()                { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien)  { this.maNhanVien = maNhanVien; }

    public String getTenBacSi()                { return tenBacSi; }
    public void   setTenBacSi(String tenBacSi) { this.tenBacSi = tenBacSi; }

    public String getTenKhoa()               { return tenKhoa; }
    public void   setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "HangCho{maHangCho=" + maHangCho + ", soNguoiCho=" + soNguoiCho +
               ", ngay=" + ngay + ", maBacSi=" + maBacSi + "}";
    }
}
