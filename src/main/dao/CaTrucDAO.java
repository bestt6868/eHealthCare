package dao;

import config.DatabaseConfig;
import model.BacSi;
import model.CaTruc;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn ca trực từ bảng CA_TRUC và LICH_CA_TRUC.
 */
public class CaTrucDAO {

    /**
     * Lấy danh sách tất cả ca trực, kèm danh sách bác sĩ phụ trách.
     *
     * @return danh sách CaTruc đầy đủ thông tin
     */
    public List<CaTruc> layDanhSachCaTruc() {
        String sqlCaTruc = "SELECT maCaTruc, tenCaTruc, gioBatDau, gioKetThuc, ngayTruc, maNhanVien "
                            + "FROM CA_TRUC "
                            + "WHERE ngayTruc > CAST(GETDATE() AS DATE) "
                            + "   OR (ngayTruc = CAST(GETDATE() AS DATE) AND gioKetThuc > CAST(GETDATE() AS TIME)) "
                            + "ORDER BY ngayTruc DESC, gioBatDau ASC";
        List<CaTruc> danhSach = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCaTruc);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CaTruc ct = mapResultSetToEntity(rs);
                ct.setDanhSachBacSi(layBacSiTheoCaTruc(conn, ct.getMaCaTruc()));
                danhSach.add(ct);
            }
        } catch (SQLException e) {
            System.err.println("[CaTrucDAO] Lỗi layDanhSachCaTruc: " + e.getMessage());
        }
        return danhSach;
    }

    /**
     * Thêm mới ca trực và gán danh sách bác sĩ phụ trách (transaction).
     *
     * @param caTruc           đối tượng CaTruc cần thêm
     * @param danhSachMaBacSi  danh sách mã bác sĩ phụ trách
     * @return true nếu thành công
     */
    public boolean themCaTruc(CaTruc caTruc, List<Integer> danhSachMaBacSi) {
        String sqlInsertCa = "INSERT INTO CA_TRUC (tenCaTruc, gioBatDau, gioKetThuc, ngayTruc, maNhanVien) "
                           + "VALUES (?, ?, ?, ?, ?)";
        String sqlInsertLich = "INSERT INTO LICH_CA_TRUC (maCaTruc, maBacSi) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int maCaTrucMoi = insertCaTruc(conn, sqlInsertCa, caTruc);
                insertLichCaTruc(conn, sqlInsertLich, maCaTrucMoi, danhSachMaBacSi);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[CaTrucDAO] Lỗi themCaTruc – rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[CaTrucDAO] Lỗi kết nối: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa ca trực: xóa LICH_CA_TRUC trước, sau đó xóa CA_TRUC.
     *
     * @param maCaTruc mã ca trực cần xóa
     * @return true nếu xóa thành công
     */
    public boolean xoaCaTruc(int maCaTruc) {
        String sqlLayThongTin = "SELECT ngayTruc FROM CA_TRUC WHERE maCaTruc = ?";
        String sqlLayBacSi    = "SELECT maBacSi FROM LICH_CA_TRUC WHERE maCaTruc = ?";
        String sqlXoaHangCho  = "DELETE FROM HANG_CHO WHERE maBacSi = ? AND ngay = ?";
        String sqlXoaLich     = "DELETE FROM LICH_CA_TRUC WHERE maCaTruc = ?";
        String sqlXoaCa       = "DELETE FROM CA_TRUC WHERE maCaTruc = ?";
    
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Lấy ngayTruc
                LocalDate ngay = null;
                try (PreparedStatement ps = conn.prepareStatement(sqlLayThongTin)) {
                    ps.setInt(1, maCaTruc);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) ngay = rs.getDate(1).toLocalDate();
                    }
                }
                // 2. Lấy danh sách bác sĩ + xóa hàng chờ tương ứng
                if (ngay != null) {
                    List<Integer> dsBacSi = new ArrayList<>();
                    try (PreparedStatement ps = conn.prepareStatement(sqlLayBacSi)) {
                        ps.setInt(1, maCaTruc);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) dsBacSi.add(rs.getInt(1));
                        }
                    }
                    if (!dsBacSi.isEmpty()) {
                        try (PreparedStatement psXoaHC = conn.prepareStatement(sqlXoaHangCho)) {
                            for (int maBacSi : dsBacSi) {
                                psXoaHC.setInt(1, maBacSi);
                                psXoaHC.setDate(2, Date.valueOf(ngay));
                                psXoaHC.addBatch();
                            }
                            psXoaHC.executeBatch();
                        }
                    }
                }
                // 3. Xóa lịch + ca trực
                try (PreparedStatement ps1 = conn.prepareStatement(sqlXoaLich);
                     PreparedStatement ps2 = conn.prepareStatement(sqlXoaCa)) {
                    ps1.setInt(1, maCaTruc);
                    ps1.executeUpdate();
                    ps2.setInt(1, maCaTruc);
                    ps2.executeUpdate();
                }
    
                conn.commit();
                return true;
    
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[CaTrucDAO] Lỗi xoaCaTruc – rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[CaTrucDAO] Lỗi kết nối khi xóa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra xung đột lịch: bác sĩ đã có ca trực chồng khung giờ chưa.
     *
     * @param maBacSi    mã bác sĩ
     * @param ngay       ngày trực
     * @param gioBatDau  giờ bắt đầu ca mới
     * @param gioKetThuc giờ kết thúc ca mới
     * @return true nếu bị xung đột
     */
    public boolean kiemTraXungDot(int maBacSi, LocalDate ngay,
                                  LocalTime gioBatDau, LocalTime gioKetThuc) {
        String sql = "SELECT COUNT(*) FROM CA_TRUC ct "
                   + "INNER JOIN LICH_CA_TRUC lct ON lct.maCaTruc = ct.maCaTruc "
                   + "WHERE lct.maBacSi = ? AND ct.ngayTruc = ? "
                   + "AND ct.gioBatDau < ? AND ct.gioKetThuc > ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maBacSi);
            ps.setDate(2, Date.valueOf(ngay));
            ps.setTime(3, Time.valueOf(gioKetThuc));
            ps.setTime(4, Time.valueOf(gioBatDau));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[CaTrucDAO] Lỗi kiemTraXungDot: " + e.getMessage());
        }
        return false;
    }

    // ─── Phương thức nội bộ ───────────────────────────────────

    /** Thực hiện INSERT CA_TRUC và trả về maCaTruc mới. */
    private int insertCaTruc(Connection conn, String sql, CaTruc caTruc) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, caTruc.getTenCaTruc());
            ps.setTime(2, Time.valueOf(caTruc.getGioBatDau()));
            ps.setTime(3, Time.valueOf(caTruc.getGioKetThuc()));
            ps.setDate(4, Date.valueOf(caTruc.getNgayTruc()));
            ps.setInt(5, caTruc.getMaNhanVien());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Không lấy được maCaTruc sau khi INSERT");
    }

    /** Thực hiện INSERT LICH_CA_TRUC cho từng bác sĩ. */
    private void insertLichCaTruc(Connection conn, String sql,
                                  int maCaTruc, List<Integer> dsMaBacSi) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int maBacSi : dsMaBacSi) {
                ps.setInt(1, maCaTruc);
                ps.setInt(2, maBacSi);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Lấy danh sách bác sĩ theo maCaTruc, dùng Connection sẵn có. */
    private List<BacSi> layBacSiTheoCaTruc(Connection conn, int maCaTruc) throws SQLException {
        String sql = "SELECT bs.maBacSi, bs.tenBacSi, bs.maKhoa, bs.trangThai, "
                   + "       bs.maNhanVien, 0 AS soNguoiCho, ck.tenKhoa "
                   + "FROM BAC_SI bs "
                   + "INNER JOIN LICH_CA_TRUC lct ON lct.maBacSi = bs.maBacSi "
                   + "INNER JOIN CHUYEN_KHOA ck ON ck.maKhoa = bs.maKhoa "
                   + "WHERE lct.maCaTruc = ?";

        List<BacSi> danhSach = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCaTruc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BacSi bs = new BacSi();
                    bs.setMaBacSi(rs.getInt("maBacSi"));
                    bs.setTenBacSi(rs.getString("tenBacSi"));
                    bs.setMaKhoa(rs.getInt("maKhoa"));
                    bs.setTrangThai(rs.getBoolean("trangThai"));
                    bs.setMaNhanVien(rs.getInt("maNhanVien"));
                    bs.setTenKhoa(rs.getString("tenKhoa"));
                    danhSach.add(bs);
                }
            }
        }
        return danhSach;
    }

    /** Ánh xạ hàng dữ liệu từ ResultSet sang đối tượng CaTruc. */
    private CaTruc mapResultSetToEntity(ResultSet rs) throws SQLException {
        CaTruc ct = new CaTruc();
        ct.setMaCaTruc(rs.getInt("maCaTruc"));
        ct.setTenCaTruc(rs.getString("tenCaTruc"));
        Time gioBD = rs.getTime("gioBatDau");
        if (gioBD != null) ct.setGioBatDau(gioBD.toLocalTime());
        Time gioKT = rs.getTime("gioKetThuc");
        if (gioKT != null) ct.setGioKetThuc(gioKT.toLocalTime());
        Date ngay = rs.getDate("ngayTruc");
        if (ngay != null) ct.setNgayTruc(ngay.toLocalDate());
        ct.setMaNhanVien(rs.getInt("maNhanVien"));
        return ct;
    }
}
