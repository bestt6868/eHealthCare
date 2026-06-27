package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho ca trực của bác sĩ.
 * Ánh xạ với bảng CA_TRUC trong cơ sở dữ liệu.
 */
public class CaTruc {

    private int       maCaTruc;
    private String    tenCaTruc;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private LocalDate ngayTruc;
    private int       maNhanVien;

    // Danh sách bác sĩ trong ca – lấy từ JOIN LICH_CA_TRUC, không lưu DB
    private List<BacSi> danhSachBacSi = new ArrayList<>();

    /** Constructor mặc định */
    public CaTruc() {}

    /** Constructor đầy đủ tham số */
    public CaTruc(int maCaTruc, String tenCaTruc, LocalTime gioBatDau,
                  LocalTime gioKetThuc, LocalDate ngayTruc, int maNhanVien) {
        this.maCaTruc   = maCaTruc;
        this.tenCaTruc  = tenCaTruc;
        this.gioBatDau  = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.ngayTruc   = ngayTruc;
        this.maNhanVien = maNhanVien;
    }

    public int  getMaCaTruc()               { return maCaTruc; }
    public void setMaCaTruc(int maCaTruc)   { this.maCaTruc = maCaTruc; }

    public String getTenCaTruc()                   { return tenCaTruc; }
    public void   setTenCaTruc(String tenCaTruc)   { this.tenCaTruc = tenCaTruc; }

    public LocalTime getGioBatDau()                    { return gioBatDau; }
    public void      setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }

    public LocalTime getGioKetThuc()                       { return gioKetThuc; }
    public void      setGioKetThuc(LocalTime gioKetThuc)   { this.gioKetThuc = gioKetThuc; }

    public LocalDate getNgayTruc()                   { return ngayTruc; }
    public void      setNgayTruc(LocalDate ngayTruc) { this.ngayTruc = ngayTruc; }

    public int  getMaNhanVien()                { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien)  { this.maNhanVien = maNhanVien; }

    public List<BacSi> getDanhSachBacSi()                      { return danhSachBacSi; }
    public void        setDanhSachBacSi(List<BacSi> dsBacSi)   { this.danhSachBacSi = dsBacSi; }

    @Override
    public String toString() {
        return "CaTruc{maCaTruc=" + maCaTruc + ", tenCaTruc='" + tenCaTruc +
               "', ngayTruc=" + ngayTruc + ", gioBatDau=" + gioBatDau +
               ", gioKetThuc=" + gioKetThuc + "}";
    }
}
