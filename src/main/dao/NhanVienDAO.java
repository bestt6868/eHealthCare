package dao;

import config.DatabaseConfig;
import model.NhanVienQuanLy;
import util.ValidationUtil;

import java.sql.*;

/**
 * DAO xử lý truy vấn nhân viên quản lý từ bảng NHAN_VIEN_QUAN_LY.
 */
public class NhanVienDAO {

    /**
     * Xác thực đăng nhập bằng cách hash mật khẩu và so sánh với DB.
     *
     * @param maNhanVienStr mã nhân viên (nhập dạng chuỗi từ giao diện)
     * @param matKhau       mật khẩu plain text
     * @return đối tượng NhanVienQuanLy nếu đúng, null nếu sai
     */
    public NhanVienQuanLy xacThucDangNhap(String maNhanVienStr, String matKhau) {
        // Kiểm tra đầu vào cơ bản
        if (ValidationUtil.laRong(maNhanVienStr) || ValidationUtil.laRong(matKhau)) {
            return null;
        }

        int maNhanVien;
        try {
            maNhanVien = Integer.parseInt(maNhanVienStr.trim());
        } catch (NumberFormatException e) {
            return null;  // Mã nhân viên phải là số
        }

        String matKhauHash = ValidationUtil.hashSHA256(matKhau);
        String sql = "SELECT maNhanVien, hoTenNhanVien, matKhauHash "
                   + "FROM NHAN_VIEN_QUAN_LY "
                   + "WHERE maNhanVien = ? AND matKhauHash = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNhanVien);
            ps.setString(2, matKhauHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhanVienQuanLy(
                        rs.getInt("maNhanVien"),
                        rs.getString("hoTenNhanVien"),
                        rs.getString("matKhauHash")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[NhanVienDAO] Lỗi xacThucDangNhap: " + e.getMessage());
        }
        return null;
    }
}
