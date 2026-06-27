package controller;

import dao.BacSiDAO;
import model.BacSi;

import java.util.List;

/**
 * Controller xử lý nghiệp vụ quản lý bác sĩ cho phân hệ Admin.
 */
public class BacSiController {

    private final BacSiDAO bacSiDAO = new BacSiDAO();

    /**
     * Lấy toàn bộ danh sách bác sĩ.
     *
     * @return danh sách BacSi
     */
    public List<BacSi> layTatCaBacSi() {
        return bacSiDAO.layTatCaBacSi();
    }

    /**
     * Lấy danh sách bác sĩ theo chuyên khoa.
     *
     * @param maKhoa mã chuyên khoa
     * @return danh sách BacSi
     */
    public List<BacSi> layBacSiTheoKhoa(int maKhoa) {
        return bacSiDAO.layDanhSachBacSiTheoKhoa(maKhoa);
    }

    /**
     * Thêm mới bác sĩ vào hệ thống.
     *
     * @param bacSi đối tượng BacSi cần thêm
     * @return true nếu thành công
     * @throws IllegalArgumentException nếu tên bác sĩ rỗng
     */
    public boolean themBacSi(BacSi bacSi) {
        if (bacSi.getTenBacSi() == null || bacSi.getTenBacSi().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bác sĩ không được để trống.");
        }
        return bacSiDAO.themBacSi(bacSi);
    }

    /**
     * Cập nhật thông tin bác sĩ.
     *
     * @param bacSi đối tượng BacSi với dữ liệu mới
     * @return true nếu thành công
     */
    public boolean capNhatBacSi(BacSi bacSi) {
        return bacSiDAO.capNhatBacSi(bacSi);
    }

    /**
     * Xóa bác sĩ khỏi hệ thống.
     *
     * @param maBacSi mã bác sĩ cần xóa
     * @return true nếu thành công
     */
    public boolean xoaBacSi(int maBacSi) {
        return bacSiDAO.xoaBacSi(maBacSi);
    }

    /**
     * Bật hoặc tắt trạng thái hoạt động của bác sĩ.
     *
     * @param maBacSi   mã bác sĩ
     * @param trangThai true = bật, false = tắt
     * @return true nếu thành công
     */
    public boolean capNhatTrangThai(int maBacSi, boolean trangThai) {
        return bacSiDAO.capNhatTrangThai(maBacSi, trangThai);
    }
}
