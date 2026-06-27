package model;

/**
 * Entity đại diện cho nhân viên quản lý hệ thống.
 * Ánh xạ với bảng NHAN_VIEN_QUAN_LY trong cơ sở dữ liệu.
 */
public class NhanVienQuanLy {

    private int    maNhanVien;
    private String hoTenNhanVien;
    private String matKhauHash;   // mật khẩu đã hash SHA-256, không dùng plain text

    /** Constructor mặc định */
    public NhanVienQuanLy() {}

    /** Constructor đầy đủ tham số */
    public NhanVienQuanLy(int maNhanVien, String hoTenNhanVien, String matKhauHash) {
        this.maNhanVien    = maNhanVien;
        this.hoTenNhanVien = hoTenNhanVien;
        this.matKhauHash   = matKhauHash;
    }

    public int  getMaNhanVien()                { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien)  { this.maNhanVien = maNhanVien; }

    public String getHoTenNhanVien()                       { return hoTenNhanVien; }
    public void   setHoTenNhanVien(String hoTenNhanVien)   { this.hoTenNhanVien = hoTenNhanVien; }

    public String getMatKhauHash()                     { return matKhauHash; }
    public void   setMatKhauHash(String matKhauHash)   { this.matKhauHash = matKhauHash; }

    @Override
    public String toString() {
        return "NhanVienQuanLy{maNhanVien=" + maNhanVien +
               ", hoTenNhanVien='" + hoTenNhanVien + "'}";
    }
}
