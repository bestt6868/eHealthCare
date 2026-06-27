package dao;

import config.DatabaseConfig;
import model.HangCho;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn hàng chờ khám bệnh từ bảng HANG_CHO.
 */
public class HangChoDAO {

    /**
     * Lấy hàng chờ của bác sĩ theo ngày.
     * Nếu chưa tồn tại → tự động tạo mới soNguoiCho = 0.
     */
    public HangCho layHoatDongTheoBacSiVaNgay(int maBacSi, LocalDate ngay) {
        HangCho hc = timKiem(maBacSi, ngay);
        return hc != null ? hc : taoMoi(maBacSi, ngay);
    }

    /**
     * Tăng số người chờ +1, trả về số thứ tự mới.
     * Dùng 1 Connection duy nhất để tránh race condition.
     */
    public int tangSoNguoiCho(int maHangCho) {
        String sqlUp  = "UPDATE HANG_CHO SET soNguoiCho=soNguoiCho+1 WHERE maHangCho=?";
        String sqlSel = "SELECT soNguoiCho FROM HANG_CHO WHERE maHangCho=?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUp)) {
                ps.setInt(1, maHangCho); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlSel)) {
                ps.setInt(1, maHangCho);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("soNguoiCho");
                }
            }
        } catch (SQLException e) {
            System.err.println("[HangChoDAO] tangSoNguoiCho: " + e.getMessage());
        }
        return -1;
    }

    /** Lấy toàn bộ hàng chờ hôm nay kèm tên bác sĩ và tên khoa. */
    public List<HangCho> xemTatCaHangChoHomNay() {
        String sql =
            "SELECT hc.maHangCho, hc.soNguoiCho, hc.ngay, hc.maBacSi, hc.maNhanVien, "
          + "       bs.tenBacSi, ck.tenKhoa "
          + "FROM HANG_CHO hc "
          + "INNER JOIN BAC_SI bs ON bs.maBacSi = hc.maBacSi "
          + "INNER JOIN CHUYEN_KHOA ck ON ck.maKhoa = bs.maKhoa "
          + "WHERE hc.ngay = CAST(GETDATE() AS DATE) "
          + "ORDER BY ck.tenKhoa, bs.tenBacSi";

        List<HangCho> ds = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.add(mapFull(rs));
        } catch (SQLException e) {
            System.err.println("[HangChoDAO] xemTatCaHangChoHomNay: " + e.getMessage());
        }
        return ds;
    }

    /**
     * Reset hàng chờ hôm nay: xóa toàn bộ PHIEU_KHAM và HANG_CHO trong ngày.
     * FIX: Trước đây chỉ UPDATE soNguoiCho=0, không xóa hàng HANG_CHO và PHIEU_KHAM.
     */
    public boolean resetTatCaHangCho() {
        String sqlPhieu   = "DELETE FROM PHIEU_KHAM WHERE ngayKham = CAST(GETDATE() AS DATE)";
        String sqlHangCho = "DELETE FROM HANG_CHO   WHERE ngay     = CAST(GETDATE() AS DATE)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlPhieu);
                 PreparedStatement ps2 = conn.prepareStatement(sqlHangCho)) {
                ps1.executeUpdate();
                ps2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[HangChoDAO] resetTatCaHangCho rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[HangChoDAO] resetTatCaHangCho kết nối: " + e.getMessage());
            return false;
        }
    }

    // ─── helpers ─────────────────────────────────────────────

    private HangCho timKiem(int maBacSi, LocalDate ngay) {
        String sql = "SELECT maHangCho,soNguoiCho,ngay,maBacSi,maNhanVien "
                   + "FROM HANG_CHO WHERE maBacSi=? AND ngay=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBacSi);
            ps.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSimple(rs);
            }
        } catch (SQLException e) {
            System.err.println("[HangChoDAO] timKiem: " + e.getMessage());
        }
        return null;
    }

    private HangCho taoMoi(int maBacSi, LocalDate ngay) {
        String sql = "INSERT INTO HANG_CHO(soNguoiCho,ngay,maBacSi,maNhanVien) VALUES(0,?,?,1)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(ngay));
            ps.setInt(2, maBacSi);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new HangCho(keys.getInt(1), 0, ngay, maBacSi, 1);
            }
        } catch (SQLException e) {
            System.err.println("[HangChoDAO] taoMoi: " + e.getMessage());
        }
        return null;
    }

    private HangCho mapFull(ResultSet rs) throws SQLException {
        HangCho hc = mapSimple(rs);
        hc.setTenBacSi(rs.getString("tenBacSi"));
        hc.setTenKhoa(rs.getString("tenKhoa"));
        return hc;
    }

    private HangCho mapSimple(ResultSet rs) throws SQLException {
        HangCho hc = new HangCho();
        hc.setMaHangCho(rs.getInt("maHangCho"));
        hc.setSoNguoiCho(rs.getInt("soNguoiCho"));
        Date d = rs.getDate("ngay");
        if (d != null) hc.setNgay(d.toLocalDate());
        hc.setMaBacSi(rs.getInt("maBacSi"));
        hc.setMaNhanVien(rs.getInt("maNhanVien"));
        return hc;
    }
}
