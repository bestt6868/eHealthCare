package controller;

import dao.HangChoDAO;
import model.HangCho;

import java.util.List;

/**
 * Controller xử lý nghiệp vụ quản lý hàng chờ cho phân hệ Admin.
 */
public class HangChoController {

    private final HangChoDAO hangChoDAO = new HangChoDAO();

    /**
     * Lấy toàn bộ hàng chờ hôm nay (JOIN bác sĩ, khoa).
     *
     * @return danh sách HangCho hôm nay
     */
    public List<HangCho> xemHangChoHomNay() {
        return hangChoDAO.xemTatCaHangChoHomNay();
    }

    /**
     * Reset toàn bộ hàng chờ hôm nay, xóa phiếu khám trong ngày.
     *
     * @return true nếu reset thành công
     */
    public boolean resetHangCho() {
        return hangChoDAO.resetTatCaHangCho();
    }
}
