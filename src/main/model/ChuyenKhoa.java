package model;

/**
 * Entity đại diện cho chuyên khoa bệnh viện.
 * Ánh xạ với bảng CHUYEN_KHOA trong cơ sở dữ liệu.
 */
public class ChuyenKhoa {

    private int     maKhoa;
    private String  tenKhoa;
    private boolean trangThai;   // true = hoạt động, false = tắt
    private int     maNhanVien;

    /** Constructor mặc định */
    public ChuyenKhoa() {}

    /** Constructor đầy đủ tham số */
    public ChuyenKhoa(int maKhoa, String tenKhoa, boolean trangThai, int maNhanVien) {
        this.maKhoa     = maKhoa;
        this.tenKhoa    = tenKhoa;
        this.trangThai  = trangThai;
        this.maNhanVien = maNhanVien;
    }

    // ─── Getters & Setters ───────────────────────────────────

    public int  getMaKhoa()           { return maKhoa; }
    public void setMaKhoa(int maKhoa) { this.maKhoa = maKhoa; }

    public String getTenKhoa()                { return tenKhoa; }
    public void   setTenKhoa(String tenKhoa)  { this.tenKhoa = tenKhoa; }

    public boolean isTrangThai()                   { return trangThai; }
    public void    setTrangThai(boolean trangThai)  { this.trangThai = trangThai; }

    public int  getMaNhanVien()                { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien)  { this.maNhanVien = maNhanVien; }

    @Override
    public String toString() {
        return "ChuyenKhoa{maKhoa=" + maKhoa + ", tenKhoa='" + tenKhoa +
               "', trangThai=" + trangThai + "}";
    }
}
