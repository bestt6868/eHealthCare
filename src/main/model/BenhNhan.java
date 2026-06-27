package model;

import java.time.LocalDate;

/**
 * Entity đại diện cho bệnh nhân trong hệ thống.
 * Ánh xạ với bảng BENH_NHAN trong cơ sở dữ liệu.
 */
public class BenhNhan {

    private String     maBaoHiemYTe;  // Mã BHYT 15 ký tự (khóa chính)
    private String     hoTen;
    private String     gioiTinh;
    private LocalDate  ngaySinh;
    private String     trangThaiThe;  // 'Con hieu luc' hoặc 'Het han'

    /** Constructor mặc định */
    public BenhNhan() {}

    /** Constructor đầy đủ tham số */
    public BenhNhan(String maBaoHiemYTe, String hoTen, String gioiTinh,
                    LocalDate ngaySinh, String trangThaiThe) {
        this.maBaoHiemYTe = maBaoHiemYTe;
        this.hoTen        = hoTen;
        this.gioiTinh     = gioiTinh;
        this.ngaySinh     = ngaySinh;
        this.trangThaiThe = trangThaiThe;
    }

    // ─── Getters & Setters ───────────────────────────────────

    public String getMaBaoHiemYTe()                    { return maBaoHiemYTe; }
    public void   setMaBaoHiemYTe(String maBaoHiemYTe) { this.maBaoHiemYTe = maBaoHiemYTe; }

    public String getHoTen()             { return hoTen; }
    public void   setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getGioiTinh()                { return gioiTinh; }
    public void   setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public LocalDate getNgaySinh()                 { return ngaySinh; }
    public void      setNgaySinh(LocalDate ngaySinh){ this.ngaySinh = ngaySinh; }

    public String getTrangThaiThe()                    { return trangThaiThe; }
    public void   setTrangThaiThe(String trangThaiThe) { this.trangThaiThe = trangThaiThe; }

    /**
     * Kiểm tra thẻ BHYT còn hiệu lực.
     * @return true nếu trạng thái là 'Con hieu luc'
     */
    public boolean laConHieuLuc() {
        return "Con hieu luc".equalsIgnoreCase(trangThaiThe);
    }

    @Override
    public String toString() {
        return "BenhNhan{maBaoHiemYTe='" + maBaoHiemYTe + "', hoTen='" + hoTen +
               "', trangThaiThe='" + trangThaiThe + "'}";
    }
}
