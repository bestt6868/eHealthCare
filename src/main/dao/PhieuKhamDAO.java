package dao;

import config.DatabaseConfig;
import model.PhieuKham;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DAO xử lý truy vấn phiếu khám bệnh từ bảng PHIEU_KHAM.
 */
public class PhieuKhamDAO {

    /**
     * Tạo phiếu khám mới và trả về mã phiếu vừa tạo.
     *
     * @param phieu đối tượng PhieuKham cần lưu
     * @return maPhieuKham vừa tạo, -1 nếu lỗi
     */
    public int taoPhieu(PhieuKham phieu) {
        String sql = "INSERT INTO PHIEU_KHAM (ngayKham, soThuTu, maHangCho, maBaoHiemYTe, ngayGioLayPhieu) "
                   + "VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(phieu.getNgayKham()));
            ps.setInt(2, phieu.getSoThuTu());
            ps.setInt(3, phieu.getMaHangCho());
            ps.setString(4, phieu.getMaBaoHiemYTe());

            int soHangAnh = ps.executeUpdate();
            if (soHangAnh == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[PhieuKhamDAO] Lỗi taoPhieu: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Lấy phiếu khám theo mã, kèm đầy đủ thông tin bệnh nhân, bác sĩ, khoa.
     *
     * @param maPhieuKham mã phiếu cần truy vấn
     * @return PhieuKham đầy đủ thông tin, hoặc null nếu không tìm thấy
     */
    public PhieuKham layPhieuTheoMa(int maPhieuKham) {
        String sql = "SELECT pk.maPhieuKham, pk.ngayKham, pk.soThuTu, pk.maHangCho, "
                   + "       pk.maBaoHiemYTe, pk.ngayGioLayPhieu, "
                   + "       bn.hoTen AS tenBenhNhan, bs.tenBacSi, ck.tenKhoa "
                   + "FROM PHIEU_KHAM pk "
                   + "INNER JOIN HANG_CHO hc ON hc.maHangCho = pk.maHangCho "
                   + "INNER JOIN BAC_SI bs ON bs.maBacSi = hc.maBacSi "
                   + "INNER JOIN CHUYEN_KHOA ck ON ck.maKhoa = bs.maKhoa "
                   + "INNER JOIN BENH_NHAN bn ON bn.maBaoHiemYTe = pk.maBaoHiemYTe "
                   + "WHERE pk.maPhieuKham = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuKham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            System.err.println("[PhieuKhamDAO] Lỗi layPhieuTheoMa: " + e.getMessage());
        }
        return null;
    }

    // ─── Phương thức nội bộ ───────────────────────────────────

    /** Ánh xạ hàng dữ liệu từ ResultSet sang đối tượng PhieuKham đầy đủ. */
    private PhieuKham mapResultSetToEntity(ResultSet rs) throws SQLException {
        PhieuKham pk = new PhieuKham();
        pk.setMaPhieuKham(rs.getInt("maPhieuKham"));

        Date ngayKham = rs.getDate("ngayKham");
        if (ngayKham != null) pk.setNgayKham(ngayKham.toLocalDate());

        pk.setSoThuTu(rs.getInt("soThuTu"));
        pk.setMaHangCho(rs.getInt("maHangCho"));
        pk.setMaBaoHiemYTe(rs.getString("maBaoHiemYTe"));

        Timestamp ngayGio = rs.getTimestamp("ngayGioLayPhieu");
        if (ngayGio != null) pk.setNgayGioLayPhieu(ngayGio.toLocalDateTime());

        pk.setTenBenhNhan(rs.getString("tenBenhNhan"));
        pk.setTenBacSi(rs.getString("tenBacSi"));
        pk.setTenKhoa(rs.getString("tenKhoa"));
        return pk;
    }
}
