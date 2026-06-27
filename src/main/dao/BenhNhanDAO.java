package dao;

import config.DatabaseConfig;
import model.BenhNhan;

import java.sql.*;
import java.time.LocalDate;

/**
 * DAO xử lý truy vấn dữ liệu bệnh nhân từ bảng BENH_NHAN.
 */
public class BenhNhanDAO {

    /**
     * Tìm kiếm bệnh nhân theo mã BHYT.
     * Trả về null nếu không tìm thấy.
     *
     * @param maBaoHiemYTe mã BHYT 15 ký tự
     * @return đối tượng BenhNhan, hoặc null nếu không tồn tại
     */
    public BenhNhan timKiemTheoBHYT(String maBaoHiemYTe) {
        String sql = "SELECT maBaoHiemYTe, hoTen, gioiTinh, ngaySinh, trangThaiThe "
                   + "FROM BENH_NHAN WHERE maBaoHiemYTe = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maBaoHiemYTe);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[BenhNhanDAO] Lỗi truy vấn BHYT: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ánh xạ hàng dữ liệu từ ResultSet sang đối tượng BenhNhan.
     */
    private BenhNhan mapResultSetToEntity(ResultSet rs) throws SQLException {
        BenhNhan bn = new BenhNhan();
        bn.setMaBaoHiemYTe(rs.getString("maBaoHiemYTe"));
        bn.setHoTen(rs.getString("hoTen"));
        bn.setGioiTinh(rs.getString("gioiTinh"));
        Date ngaySinh = rs.getDate("ngaySinh");
        if (ngaySinh != null) {
            bn.setNgaySinh(ngaySinh.toLocalDate());
        }
        bn.setTrangThaiThe(rs.getString("trangThaiThe"));
        return bn;
    }
}
