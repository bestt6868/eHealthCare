package controller;

import dao.*;
import model.*;
import util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller xử lý luồng nghiệp vụ kiosk.
 * KHÔNG gọi PrinterUtil ở đây — việc in do View đảm nhiệm trên EDT.
 */
public class KioskController {

    private final BenhNhanDAO   benhNhanDAO   = new BenhNhanDAO();
    private final ChuyenKhoaDAO chuyenKhoaDAO = new ChuyenKhoaDAO();
    private final BacSiDAO      bacSiDAO      = new BacSiDAO();
    private final HangChoDAO    hangChoDAO    = new HangChoDAO();
    private final PhieuKhamDAO  phieuKhamDAO  = new PhieuKhamDAO();

    /**
     * Xác nhận mã BHYT, trả về BenhNhan hợp lệ.
     * Ném IllegalArgumentException nếu không hợp lệ hoặc thẻ hết hạn.
     */
    public BenhNhan xacNhanBHYT(String maBHYT) {
        if (!ValidationUtil.laMaBHYTHopLe(maBHYT))
            throw new IllegalArgumentException("Mã BHYT phải đúng 15 ký tự.");

        BenhNhan bn = benhNhanDAO.timKiemTheoBHYT(maBHYT.trim());
        if (bn == null)
            throw new IllegalArgumentException("Mã BHYT không tồn tại trong hệ thống.");
        if (!bn.laConHieuLuc())
            throw new IllegalArgumentException("Thẻ BHYT đã hết hạn, không thể đăng ký khám.");
        return bn;
    }

    /** Lấy danh sách khoa hoạt động có ca trực hôm nay. */
    public List<ChuyenKhoa> layDanhSachKhoa() {
        return chuyenKhoaDAO.layDanhSachKhoaHoatDong();
    }

    /** Lấy bác sĩ theo khoa có ca trực hôm nay. */
    public List<BacSi> layDanhSachBacSiTheoKhoa(int maKhoa) {
        return bacSiDAO.layDanhSachBacSiTheoKhoa(maKhoa);
    }

    /** Tự chọn bác sĩ ít người chờ nhất có ca trực hôm nay. */
    public BacSi chonBacSiTuDong(int maKhoa) {
        BacSi bs = bacSiDAO.timBacSiItNguoiNhatTheoKhoa(maKhoa);
        if (bs == null)
            throw new RuntimeException("Không có bác sĩ khả dụng trong khoa này hôm nay.");
        return bs;
    }

    /**
     * Đăng ký khám: tạo/lấy hàng chờ, tăng số thứ tự, lưu phiếu vào DB.
     * KHÔNG in phiếu — View sẽ gọi PrinterUtil trên EDT sau khi method này trả về.
     *
     * @return PhieuKham đã lưu DB với đầy đủ thông tin hiển thị
     */
    public PhieuKham dangKy(BenhNhan benhNhan, BacSi bacSi, ChuyenKhoa khoa) {
        LocalDate homNay = LocalDate.now();

        HangCho hangCho = hangChoDAO.layHoatDongTheoBacSiVaNgay(bacSi.getMaBacSi(), homNay);
        if (hangCho == null)
            throw new RuntimeException("Không thể tạo hàng chờ. Vui lòng thử lại.");

        int soThuTu = hangChoDAO.tangSoNguoiCho(hangCho.getMaHangCho());
        if (soThuTu <= 0)
            throw new RuntimeException("Không thể cấp số thứ tự. Vui lòng thử lại.");

        PhieuKham phieu = xayDungPhieu(soThuTu, hangCho.getMaHangCho(),
                benhNhan, bacSi, khoa, homNay);

        int maMoi = phieuKhamDAO.taoPhieu(phieu);
        if (maMoi <= 0)
            throw new RuntimeException("Không thể lưu phiếu khám vào hệ thống.");

        phieu.setMaPhieuKham(maMoi);
        return phieu;
    }

    // ─── helper ──────────────────────────────────────────────

    private PhieuKham xayDungPhieu(int soThuTu, int maHangCho,
                                    BenhNhan bn, BacSi bs, ChuyenKhoa ck,
                                    LocalDate ngayKham) {
        PhieuKham p = new PhieuKham();
        p.setSoThuTu(soThuTu);
        p.setMaHangCho(maHangCho);
        p.setMaBaoHiemYTe(bn.getMaBaoHiemYTe());
        p.setNgayKham(ngayKham);
        p.setNgayGioLayPhieu(LocalDateTime.now());   // thời điểm thực tế
        // Trường hiển thị — dùng khi in phiếu
        p.setTenBenhNhan(bn.getHoTen());
        p.setTenBacSi(bs.getTenBacSi());
        p.setTenKhoa(ck.getTenKhoa());
        return p;
    }
}
