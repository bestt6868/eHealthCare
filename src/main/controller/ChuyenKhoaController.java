package controller;

import dao.ChuyenKhoaDAO;
import model.ChuyenKhoa;

import java.util.List;

/**
 * Controller xử lý nghiệp vụ quản lý chuyên khoa cho phân hệ Admin.
 */
public class ChuyenKhoaController {

    private final ChuyenKhoaDAO chuyenKhoaDAO = new ChuyenKhoaDAO();

    /**
     * Lấy toàn bộ danh sách chuyên khoa.
     *
     * @return danh sách tất cả ChuyenKhoa
     */
    public List<ChuyenKhoa> layTatCaKhoa() {
        return chuyenKhoaDAO.layTatCaKhoa();
    }

    /**
     * Thêm mới chuyên khoa.
     *
     * @param khoa đối tượng ChuyenKhoa cần thêm
     * @return true nếu thêm thành công
     * @throws IllegalArgumentException nếu tên khoa rỗng hoặc đã tồn tại
     */
    public boolean themKhoa(ChuyenKhoa khoa) {
        if (khoa.getTenKhoa() == null || khoa.getTenKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoa không được để trống.");
        }
        if (chuyenKhoaDAO.kiemTraTenTonTai(khoa.getTenKhoa())) {
            throw new IllegalArgumentException("Khoa đã tồn tại trong hệ thống.");
        }
        return chuyenKhoaDAO.themKhoa(khoa);
    }

    /**
     * Xóa chuyên khoa khỏi hệ thống.
     *
     * @param maKhoa mã khoa cần xóa
     * @return true nếu xóa thành công
     */
    public boolean xoaKhoa(int maKhoa) {
        return chuyenKhoaDAO.xoaKhoa(maKhoa);
    }

    /**
     * Bật hoặc tắt trạng thái hoạt động của khoa.
     *
     * @param maKhoa    mã khoa
     * @param trangThai true = bật, false = tắt
     * @return true nếu thành công
     */
    public boolean capNhatTrangThai(int maKhoa, boolean trangThai) {
        return chuyenKhoaDAO.capNhatTrangThai(maKhoa, trangThai);
    }
}
