package dao;

import config.DatabaseConfig;
import model.BacSi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu bác sĩ từ bảng BAC_SI.
 */
public class BacSiDAO {

    /**
     * [KIOSK] Lấy bác sĩ đang hoạt động theo khoa VÀ có ca trực hôm nay.
     * Bác sĩ không có ca trực trong ngày sẽ không hiện trong kiosk.
     */
    public List<BacSi> layDanhSachBacSiTheoKhoa(int maKhoa) {
        String sql =
            "SELECT DISTINCT bs.maBacSi, bs.tenBacSi, bs.maKhoa, bs.trangThai, bs.maNhanVien, "
          + "       ISNULL(hc.soNguoiCho, 0) AS soNguoiCho, ck.tenKhoa "
          + "FROM BAC_SI bs "
          + "INNER JOIN CHUYEN_KHOA ck ON ck.maKhoa = bs.maKhoa "
          + "INNER JOIN LICH_CA_TRUC lct ON lct.maBacSi = bs.maBacSi "
          + "INNER JOIN CA_TRUC ct ON ct.maCaTruc = lct.maCaTruc "
          + "     AND ct.ngayTruc = CAST(GETDATE() AS DATE) "
          + "     AND CAST(GETDATE() AS TIME) BETWEEN ct.gioBatDau AND ct.gioKetThuc "
          + "LEFT  JOIN HANG_CHO hc ON hc.maBacSi = bs.maBacSi "
          + "     AND hc.ngay = CAST(GETDATE() AS DATE) "
          + "WHERE bs.maKhoa = ? AND bs.trangThai = 1 "
          + "ORDER BY ISNULL(hc.soNguoiCho,0) ASC, bs.tenBacSi ASC";

        List<BacSi> ds = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maKhoa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] layDanhSachBacSiTheoKhoa: " + e.getMessage());
        }
        return ds;
    }

    /**
     * [ADMIN] Lấy toàn bộ bác sĩ (không lọc ca trực), kèm tên khoa và số chờ hôm nay.
     */
    public List<BacSi> layTatCaBacSi() {
        String sql =
            "SELECT bs.maBacSi, bs.tenBacSi, bs.maKhoa, bs.trangThai, bs.maNhanVien, "
          + "       ISNULL(hc.soNguoiCho,0) AS soNguoiCho, ck.tenKhoa "
          + "FROM BAC_SI bs "
          + "INNER JOIN CHUYEN_KHOA ck ON ck.maKhoa = bs.maKhoa "
          + "LEFT  JOIN HANG_CHO hc ON hc.maBacSi = bs.maBacSi "
          + "     AND hc.ngay = CAST(GETDATE() AS DATE) "
          + "ORDER BY ck.tenKhoa, bs.tenBacSi";

        List<BacSi> ds = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] layTatCaBacSi: " + e.getMessage());
        }
        return ds;
    }

    /** Thêm bác sĩ mới. */
    public boolean themBacSi(BacSi bs) {
        String sql = "INSERT INTO BAC_SI (tenBacSi,maKhoa,trangThai,maNhanVien) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bs.getTenBacSi());
            ps.setInt(2, bs.getMaKhoa());
            ps.setBoolean(3, bs.isTrangThai());
            ps.setInt(4, bs.getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] themBacSi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa bác sĩ: cascade xóa PHIEU_KHAM → HANG_CHO → LICH_CA_TRUC → BAC_SI.
     * FIX: Trước đây thất bại do vi phạm FK constraint.
     */
    public boolean xoaBacSi(int maBacSi) {
        String sqlPhieu  = "DELETE FROM PHIEU_KHAM WHERE maHangCho IN "
                         + "(SELECT maHangCho FROM HANG_CHO WHERE maBacSi = ?)";
        String sqlHangCho = "DELETE FROM HANG_CHO WHERE maBacSi = ?";
        String sqlLich    = "DELETE FROM LICH_CA_TRUC WHERE maBacSi = ?";
        String sqlBacSi   = "DELETE FROM BAC_SI WHERE maBacSi = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                thucThiXoa(conn, sqlPhieu,   maBacSi);
                thucThiXoa(conn, sqlHangCho, maBacSi);
                thucThiXoa(conn, sqlLich,    maBacSi);
                thucThiXoa(conn, sqlBacSi,   maBacSi);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[BacSiDAO] xoaBacSi rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] xoaBacSi kết nối: " + e.getMessage());
            return false;
        }
    }

    /** Cập nhật tên, khoa, trạng thái bác sĩ. */
    public boolean capNhatBacSi(BacSi bs) {
        String sql = "UPDATE BAC_SI SET tenBacSi=?,maKhoa=?,trangThai=? WHERE maBacSi=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bs.getTenBacSi());
            ps.setInt(2, bs.getMaKhoa());
            ps.setBoolean(3, bs.isTrangThai());
            ps.setInt(4, bs.getMaBacSi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] capNhatBacSi: " + e.getMessage());
            return false;
        }
    }

    /** Cập nhật trạng thái hoạt động (bật/tắt). */
    public boolean capNhatTrangThai(int maBacSi, boolean trangThai) {
        String sql = "UPDATE BAC_SI SET trangThai=? WHERE maBacSi=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setInt(2, maBacSi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] capNhatTrangThai: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm bác sĩ ít người chờ nhất theo khoa hôm nay (có ca trực).
     * Nhiều bác sĩ bằng nhau → chọn ngẫu nhiên qua ORDER BY NEWID().
     */
    public BacSi timBacSiItNguoiNhatTheoKhoa(int maKhoa) {
        String sql =
            "SELECT TOP 1 bs.maBacSi, bs.tenBacSi, bs.maKhoa, bs.trangThai, "
          + "       bs.maNhanVien, ISNULL(hc.soNguoiCho,0) AS soNguoiCho, ck.tenKhoa "
          + "FROM BAC_SI bs "
          + "INNER JOIN CHUYEN_KHOA ck ON ck.maKhoa = bs.maKhoa "
          + "INNER JOIN LICH_CA_TRUC lct ON lct.maBacSi = bs.maBacSi "
          + "INNER JOIN CA_TRUC ct ON ct.maCaTruc = lct.maCaTruc "
          + "     AND ct.ngayTruc = CAST(GETDATE() AS DATE) "
          + "     AND CAST(GETDATE() AS TIME) BETWEEN ct.gioBatDau AND ct.gioKetThuc "
          + "LEFT  JOIN HANG_CHO hc ON hc.maBacSi = bs.maBacSi "
          + "     AND hc.ngay = CAST(GETDATE() AS DATE) "
          + "WHERE bs.maKhoa = ? AND bs.trangThai = 1 "
          + "ORDER BY ISNULL(hc.soNguoiCho,0) ASC, NEWID()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maKhoa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[BacSiDAO] timBacSiItNguoiNhat: " + e.getMessage());
        }
        return null;
    }

    // ─── helpers ─────────────────────────────────────────────

    private BacSi mapRow(ResultSet rs) throws SQLException {
        BacSi bs = new BacSi();
        bs.setMaBacSi(rs.getInt("maBacSi"));
        bs.setTenBacSi(rs.getString("tenBacSi"));
        bs.setMaKhoa(rs.getInt("maKhoa"));
        bs.setTrangThai(rs.getBoolean("trangThai"));
        bs.setMaNhanVien(rs.getInt("maNhanVien"));
        bs.setSoNguoiCho(rs.getInt("soNguoiCho"));
        bs.setTenKhoa(rs.getString("tenKhoa"));
        return bs;
    }

    private void thucThiXoa(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
