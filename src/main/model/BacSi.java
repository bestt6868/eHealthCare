package model;

/**
 * Entity đại diện cho bác sĩ trong hệ thống.
 * Ánh xạ với bảng BAC_SI trong cơ sở dữ liệu.
 */
public class BacSi {

    private int     maBacSi;
    private String  tenBacSi;
    private int     maKhoa;
    private boolean trangThai;   // true = hoạt động, false = tắt
    private int     maNhanVien;
    private int     soNguoiCho;  // tính toán từ HANG_CHO, không lưu DB
    private String  tenKhoa;     // lấy từ JOIN, không lưu DB

    /** Constructor mặc định */
    public BacSi() {}

    /** Constructor đầy đủ tham số (không bao gồm soNguoiCho và tenKhoa) */
    public BacSi(int maBacSi, String tenBacSi, int maKhoa,
                 boolean trangThai, int maNhanVien) {
        this.maBacSi    = maBacSi;
        this.tenBacSi   = tenBacSi;
        this.maKhoa     = maKhoa;
        this.trangThai  = trangThai;
        this.maNhanVien = maNhanVien;
    }

    // ─── Getters & Setters ───────────────────────────────────

    public int    getMaBacSi()              { return maBacSi; }
    public void   setMaBacSi(int maBacSi)   { this.maBacSi = maBacSi; }

    public String getTenBacSi()                { return tenBacSi; }
    public void   setTenBacSi(String tenBacSi) { this.tenBacSi = tenBacSi; }

    public int  getMaKhoa()            { return maKhoa; }
    public void setMaKhoa(int maKhoa)  { this.maKhoa = maKhoa; }

    public boolean isTrangThai()                   { return trangThai; }
    public void    setTrangThai(boolean trangThai)  { this.trangThai = trangThai; }

    public int  getMaNhanVien()                { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien)  { this.maNhanVien = maNhanVien; }

    public int  getSoNguoiCho()                { return soNguoiCho; }
    public void setSoNguoiCho(int soNguoiCho)  { this.soNguoiCho = soNguoiCho; }

    public String getTenKhoa()                 { return tenKhoa; }
    public void   setTenKhoa(String tenKhoa)   { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "BacSi{maBacSi=" + maBacSi + ", tenBacSi='" + tenBacSi +
               "', maKhoa=" + maKhoa + ", soNguoiCho=" + soNguoiCho + "}";
    }
}
