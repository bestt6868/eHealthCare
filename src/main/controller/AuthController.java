package controller;

import dao.NhanVienDAO;
import model.NhanVienQuanLy;

/**
 * Controller xử lý xác thực đăng nhập và quản lý phiên làm việc.
 */
public class AuthController {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private NhanVienQuanLy    nhanVienHienTai = null;

    /**
     * Đăng nhập hệ thống quản trị.
     *
     * @param maNhanVien mã nhân viên
     * @param matKhau    mật khẩu plain text
     * @return NhanVienQuanLy nếu đúng, null nếu sai
     */
    public NhanVienQuanLy dangNhap(String maNhanVien, String matKhau) {
        nhanVienHienTai = nhanVienDAO.xacThucDangNhap(maNhanVien, matKhau);
        return nhanVienHienTai;
    }

    /**
     * Đăng xuất, xóa phiên làm việc hiện tại.
     */
    public void dangXuat() {
        nhanVienHienTai = null;
    }

    /**
     * Kiểm tra có phiên làm việc đang hoạt động không.
     *
     * @return true nếu đã đăng nhập
     */
    public boolean coPhienLamViec() {
        return nhanVienHienTai != null;
    }

    /**
     * Lấy thông tin nhân viên đang đăng nhập.
     *
     * @return NhanVienQuanLy hoặc null
     */
    public NhanVienQuanLy getNhanVienHienTai() {
        return nhanVienHienTai;
    }
}
