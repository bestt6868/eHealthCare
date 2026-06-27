package view.kiosk;

import model.BacSi;
import model.BenhNhan;
import model.ChuyenKhoa;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * JFrame chính của phân hệ Kiosk.
 * Chạy toàn màn hình, dùng CardLayout để chuyển đổi giữa 4 màn hình.
 * Lưu trữ dữ liệu phiên: bệnh nhân, chuyên khoa, bác sĩ đã chọn.
 */
public class KioskApp extends JFrame {

    // ─── Tên các màn hình trong CardLayout ───────────────────
    public static final String MAN_HINH_NHAP_BHYT    = "NHAP_BHYT";
    public static final String MAN_HINH_CHON_KHOA    = "CHON_KHOA";
    public static final String MAN_HINH_CHON_BAC_SI  = "CHON_BAC_SI";
    public static final String MAN_HINH_XAC_NHAN     = "XAC_NHAN";

    // ─── Dữ liệu phiên đăng ký ───────────────────────────────
    private BenhNhan   benhNhanHienTai;
    private ChuyenKhoa chuyenKhoaHienTai;
    private BacSi      bacSiHienTai;

    // ─── Thành phần giao diện ────────────────────────────────
    private final CardLayout  cardLayout;
    private final JPanel      panelChinh;

    private ManHinh1NhapBHYT        manHinh1;
    private ManHinh2ChonKhoa        manHinh2;
    private ManHinh3ChonBacSi       manHinh3;
    private ManHinh4XacNhanInPhieu  manHinh4;

    public KioskApp() {
        super("Hệ thống cấp phiếu khám bệnh");
        cardLayout = new CardLayout();
        panelChinh = new JPanel(cardLayout);
        khoiTaoGiaoDien();
        khoiTaoCacManHinh();
    }

    /** Thiết lập giao diện JFrame chính. */
    private void khoiTaoGiaoDien() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UIConstants.MAU_NEN);
        add(panelChinh, BorderLayout.CENTER);
    }

    /** Khởi tạo và thêm 4 màn hình vào CardLayout. */
    private void khoiTaoCacManHinh() {
        manHinh1 = new ManHinh1NhapBHYT(this);
        manHinh2 = new ManHinh2ChonKhoa(this);
        manHinh3 = new ManHinh3ChonBacSi(this);
        manHinh4 = new ManHinh4XacNhanInPhieu(this);

        panelChinh.add(manHinh1, MAN_HINH_NHAP_BHYT);
        panelChinh.add(manHinh2, MAN_HINH_CHON_KHOA);
        panelChinh.add(manHinh3, MAN_HINH_CHON_BAC_SI);
        panelChinh.add(manHinh4, MAN_HINH_XAC_NHAN);

        // Hiển thị màn hình đầu tiên
        cardLayout.show(panelChinh, MAN_HINH_NHAP_BHYT);
    }

    /**
     * Chuyển sang màn hình khác trong CardLayout.
     *
     * @param tenManHinh tên màn hình (dùng các hằng số MAN_HINH_*)
     */
    public void chuyenManHinh(String tenManHinh) {
        // Thông báo cho màn hình mới để refresh dữ liệu nếu cần
        switch (tenManHinh) {
            case MAN_HINH_CHON_KHOA:   manHinh2.khoiTaoLai(); break;
            case MAN_HINH_CHON_BAC_SI: manHinh3.khoiTaoLai(); break;
            case MAN_HINH_XAC_NHAN:    manHinh4.khoiTaoLai(); break;
            case MAN_HINH_NHAP_BHYT:   manHinh1.khoiTaoLai(); break;
        }
        cardLayout.show(panelChinh, tenManHinh);
    }

    /**
     * Reset toàn bộ phiên đăng ký, quay về màn hình nhập BHYT.
     * Gọi sau khi in phiếu thành công hoặc người dùng hủy.
     */
    public void resetPhien() {
        benhNhanHienTai    = null;
        chuyenKhoaHienTai  = null;
        bacSiHienTai       = null;
        chuyenManHinh(MAN_HINH_NHAP_BHYT);
    }

    // ─── Getters & Setters dữ liệu phiên ─────────────────────

    public BenhNhan   getBenhNhanHienTai()                        { return benhNhanHienTai; }
    public void       setBenhNhanHienTai(BenhNhan bn)             { this.benhNhanHienTai = bn; }

    public ChuyenKhoa getChuyenKhoaHienTai()                     { return chuyenKhoaHienTai; }
    public void       setChuyenKhoaHienTai(ChuyenKhoa ck)        { this.chuyenKhoaHienTai = ck; }

    public BacSi      getBacSiHienTai()                          { return bacSiHienTai; }
    public void       setBacSiHienTai(BacSi bs)                  { this.bacSiHienTai = bs; }

    /** Khởi chạy ứng dụng Kiosk trên Event Dispatch Thread. */
    public static void khoiChay() {
        SwingUtilities.invokeLater(() -> {
            KioskApp app = new KioskApp();
            app.setVisible(true);
        });
    }
}
