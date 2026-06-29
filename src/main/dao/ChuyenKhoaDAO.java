package dao;

import config.DatabaseConfig;
import model.ChuyenKhoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu chuyên khoa từ bảng CHUYEN_KHOA.
 */
public class ChuyenKhoaDAO {

    /**
     * Lấy khoa đang hoạt động VÀ có ít nhất 1 bác sĩ hoạt động có ca trực hôm nay.
     */
    public List<ChuyenKhoa> layDanhSachKhoaHoatDong() {
        String sql =
            "SELECT DISTINCT ck.maKhoa, ck.tenKhoa, ck.trangThai, ck.maNhanVien "
          + "FROM CHUYEN_KHOA ck "
          + "INNER JOIN BAC_SI bs ON bs.maKhoa = ck.maKhoa AND bs.trangThai = 1 "
          + "INNER JOIN LICH_CA_TRUC lct ON lct.maBacSi = bs.maBacSi "
          + "INNER JOIN CA_TRUC ct ON ct.maCaTruc = lct.maCaTruc "
          + "     AND ct.ngayTruc = CAST(GETDATE() AS DATE) "
          + "     AND CAST(GETDATE() AS TIME) BETWEEN ct.gioBatDau AND ct.gioKetThuc "
          + "WHERE ck.trangThai = 1 "
          + "ORDER BY ck.tenKhoa";
        return runQuery(sql);
    }

    /** Lấy toàn bộ khoa (admin). */
    public List<ChuyenKhoa> layTatCaKhoa() {
        return runQuery("SELECT maKhoa,tenKhoa,trangThai,maNhanVien FROM CHUYEN_KHOA ORDER BY tenKhoa");
    }

    /** Thêm khoa mới. */
    public boolean themKhoa(ChuyenKhoa k) {
        String sql = "INSERT INTO CHUYEN_KHOA(tenKhoa,trangThai,maNhanVien) VALUES(?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getTenKhoa());
            ps.setBoolean(2, k.isTrangThai());
            ps.setInt(3, k.getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ChuyenKhoaDAO] themKhoa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa khoa: cascade xóa toàn bộ dữ liệu liên quan trong 1 transaction.
     * Thứ tự: PHIEU_KHAM → HANG_CHO → LICH_CA_TRUC → BAC_SI → CHUYEN_KHOA.
     * FIX: Trước đây thất bại do FK constraint BAC_SI → CHUYEN_KHOA.
     */
    public boolean xoaKhoa(int maKhoa) {
        // Xóa phiếu khám qua hang chờ
        String sqlPhieu   = "DELETE FROM PHIEU_KHAM WHERE maHangCho IN "
                          + "(SELECT maHangCho FROM HANG_CHO WHERE maBacSi IN "
                          + " (SELECT maBacSi FROM BAC_SI WHERE maKhoa = ?))";
        String sqlHangCho = "DELETE FROM HANG_CHO WHERE maBacSi IN "
                          + "(SELECT maBacSi FROM BAC_SI WHERE maKhoa = ?)";
        String sqlLich    = "DELETE FROM LICH_CA_TRUC WHERE maBacSi IN "
                          + "(SELECT maBacSi FROM BAC_SI WHERE maKhoa = ?)";
        String sqlBacSi   = "DELETE FROM BAC_SI WHERE maKhoa = ?";
        String sqlKhoa    = "DELETE FROM CHUYEN_KHOA WHERE maKhoa = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (String sql : new String[]{sqlPhieu, sqlHangCho, sqlLich, sqlBacSi, sqlKhoa}) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, maKhoa);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[ChuyenKhoaDAO] xoaKhoa rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[ChuyenKhoaDAO] xoaKhoa kết nối: " + e.getMessage());
            return false;
        }
    }

    /** Bật/tắt trạng thái khoa. */
    public boolean capNhatTrangThai(int maKhoa, boolean trangThai) {
        String sql = "UPDATE CHUYEN_KHOA SET trangThai=? WHERE maKhoa=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setInt(2, maKhoa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ChuyenKhoaDAO] capNhatTrangThai: " + e.getMessage());
            return false;
        }
    }

    /** Kiểm tra tên khoa đã tồn tại chưa. */
    public boolean kiemTraTenTonTai(String tenKhoa) {
        String sql = "SELECT COUNT(*) FROM CHUYEN_KHOA WHERE tenKhoa=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenKhoa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ChuyenKhoaDAO] kiemTraTenTonTai: " + e.getMessage());
            return false;
        }
    }

    // ─── helpers ─────────────────────────────────────────────

    private List<ChuyenKhoa> runQuery(String sql) {
        List<ChuyenKhoa> ds = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ChuyenKhoaDAO] query: " + e.getMessage());
        }
        return ds;
    }

    private ChuyenKhoa mapRow(ResultSet rs) throws SQLException {
        return new ChuyenKhoa(rs.getInt("maKhoa"), rs.getString("tenKhoa"),
                rs.getBoolean("trangThai"), rs.getInt("maNhanVien"));
    }
}
