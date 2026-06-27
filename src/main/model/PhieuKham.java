package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho phiếu khám bệnh.
 * Ánh xạ với bảng PHIEU_KHAM trong cơ sở dữ liệu.
 * Các trường tenBenhNhan, tenBacSi, tenKhoa chỉ dùng để hiển thị, không lưu DB.
 */
public class PhieuKham {

    private int           maPhieuKham;
    private LocalDate     ngayKham;
    private int           soThuTu;
    private int           maHangCho;
    private String        maBaoHiemYTe;
    private LocalDateTime ngayGioLayPhieu;

    // Trường hiển thị – lấy từ JOIN, không lưu DB
    private String tenBenhNhan;
    private String tenBacSi;
    private String tenKhoa;

    /** Constructor mặc định */
    public PhieuKham() {}

    /** Constructor đầy đủ tham số (các trường lưu DB) */
    public PhieuKham(int maPhieuKham, LocalDate ngayKham, int soThuTu,
                     int maHangCho, String maBaoHiemYTe, LocalDateTime ngayGioLayPhieu) {
        this.maPhieuKham     = maPhieuKham;
        this.ngayKham        = ngayKham;
        this.soThuTu         = soThuTu;
        this.maHangCho       = maHangCho;
        this.maBaoHiemYTe    = maBaoHiemYTe;
        this.ngayGioLayPhieu = ngayGioLayPhieu;
    }

    // ─── Getters & Setters ───────────────────────────────────

    public int  getMaPhieuKham()                  { return maPhieuKham; }
    public void setMaPhieuKham(int maPhieuKham)   { this.maPhieuKham = maPhieuKham; }

    public LocalDate getNgayKham()                  { return ngayKham; }
    public void      setNgayKham(LocalDate ngayKham){ this.ngayKham = ngayKham; }

    public int  getSoThuTu()              { return soThuTu; }
    public void setSoThuTu(int soThuTu)   { this.soThuTu = soThuTu; }

    public int  getMaHangCho()                { return maHangCho; }
    public void setMaHangCho(int maHangCho)   { this.maHangCho = maHangCho; }

    public String getMaBaoHiemYTe()                      { return maBaoHiemYTe; }
    public void   setMaBaoHiemYTe(String maBaoHiemYTe)   { this.maBaoHiemYTe = maBaoHiemYTe; }

    public LocalDateTime getNgayGioLayPhieu()                          { return ngayGioLayPhieu; }
    public void          setNgayGioLayPhieu(LocalDateTime ngayGioLayPhieu){ this.ngayGioLayPhieu = ngayGioLayPhieu; }

    public String getTenBenhNhan()                     { return tenBenhNhan; }
    public void   setTenBenhNhan(String tenBenhNhan)   { this.tenBenhNhan = tenBenhNhan; }

    public String getTenBacSi()                  { return tenBacSi; }
    public void   setTenBacSi(String tenBacSi)   { this.tenBacSi = tenBacSi; }

    public String getTenKhoa()               { return tenKhoa; }
    public void   setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "PhieuKham{maPhieuKham=" + maPhieuKham + ", soThuTu=" + soThuTu +
               ", ngayKham=" + ngayKham + ", maBaoHiemYTe='" + maBaoHiemYTe + "'}";
    }
}
