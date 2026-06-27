package controller;

import dao.CaTrucDAO;
import model.BacSi;
import model.CaTruc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller xử lý nghiệp vụ quản lý ca trực cho phân hệ Admin.
 */
public class CaTrucController {

    private final CaTrucDAO caTrucDAO = new CaTrucDAO();

    /**
     * Lấy danh sách tất cả ca trực, kèm danh sách bác sĩ phụ trách.
     *
     * @return danh sách CaTruc
     */
    public List<CaTruc> layDanhSachCaTruc() {
        return caTrucDAO.layDanhSachCaTruc();
    }

    /**
     * Thêm ca trực mới và gán bác sĩ phụ trách.
     * Kiểm tra xung đột lịch trước khi thêm.
     *
     * @param caTruc          đối tượng CaTruc cần thêm
     * @param danhSachMaBacSi danh sách mã bác sĩ
     * @return danh sách tên bác sĩ bị xung đột (rỗng nếu không có xung đột và thêm thành công)
     */
    public List<String> themCaTrucVoiKiemTraXungDot(CaTruc caTruc,
                                                     List<BacSi> danhSachBacSi) {
        List<String> danhSachXungDot = new ArrayList<>();

        for (BacSi bs : danhSachBacSi) {
            boolean coXungDot = caTrucDAO.kiemTraXungDot(
                bs.getMaBacSi(), caTruc.getNgayTruc(),
                caTruc.getGioBatDau(), caTruc.getGioKetThuc()
            );
            if (coXungDot) danhSachXungDot.add(bs.getTenBacSi());
        }

        if (!danhSachXungDot.isEmpty()) {
            return danhSachXungDot;  // Trả về danh sách xung đột
        }

        // Không xung đột → thêm ca trực
        List<Integer> dsMaBacSi = new ArrayList<>();
        for (BacSi bs : danhSachBacSi) dsMaBacSi.add(bs.getMaBacSi());
        caTrucDAO.themCaTruc(caTruc, dsMaBacSi);
        return danhSachXungDot; // rỗng = thành công
    }

    /**
     * Xóa ca trực khỏi hệ thống.
     *
     * @param maCaTruc mã ca trực
     * @return true nếu xóa thành công
     */
    public boolean xoaCaTruc(int maCaTruc) {
        return caTrucDAO.xoaCaTruc(maCaTruc);
    }

    /**
     * Kiểm tra xung đột lịch cho một bác sĩ.
     *
     * @param maBacSi    mã bác sĩ
     * @param ngay       ngày trực
     * @param gioBatDau  giờ bắt đầu
     * @param gioKetThuc giờ kết thúc
     * @return true nếu bị xung đột
     */
    public boolean kiemTraXungDot(int maBacSi, LocalDate ngay,
                                  LocalTime gioBatDau, LocalTime gioKetThuc) {
        return caTrucDAO.kiemTraXungDot(maBacSi, ngay, gioBatDau, gioKetThuc);
    }
}
