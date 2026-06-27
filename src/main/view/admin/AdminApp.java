package view.admin;

import controller.AuthController;
import model.NhanVienQuanLy;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * JFrame chính của phân hệ Admin.
 * Kích thước 1200x800, dùng CardLayout chuyển giữa đăng nhập và quản trị.
 * Lưu thông tin nhân viên đang đăng nhập và đếm số lần đăng nhập sai.
 */
public class AdminApp extends JFrame {

    public static final String MAN_HINH_DANG_NHAP = "DANG_NHAP";
    public static final String MAN_HINH_CHINH     = "CHINH";

    private final AuthController authController = new AuthController();

    // ─── Phiên làm việc ──────────────────────────────────────
    private NhanVienQuanLy nhanVienHienTai;
    private int            soLanDangNhapSai = 0;

    // ─── Layout ──────────────────────────────────────────────
    private final CardLayout cardLayout  = new CardLayout();
    private final JPanel     panelChinh  = new JPanel(cardLayout);

    private DangNhapPanel    dangNhapPanel;
    private MainAdminPanel   mainAdminPanel;

    public AdminApp() {
        super("Hệ thống Quản trị – Hospital Kiosk");
        khoiTaoFrame();
        khoiTaoCacPanel();
    }

    private void khoiTaoFrame() {
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        getContentPane().setBackground(UIConstants.MAU_NEN);
        add(panelChinh, BorderLayout.CENTER);
    }

    private void khoiTaoCacPanel() {
        dangNhapPanel  = new DangNhapPanel(this);
        mainAdminPanel = new MainAdminPanel(this);

        panelChinh.add(dangNhapPanel,  MAN_HINH_DANG_NHAP);
        panelChinh.add(mainAdminPanel, MAN_HINH_CHINH);

        cardLayout.show(panelChinh, MAN_HINH_DANG_NHAP);
    }

    /**
     * Xử lý đăng nhập: xác thực, đếm lần sai, chuyển màn hình.
     *
     * @param maNhanVien mã nhân viên
     * @param matKhau    mật khẩu plain text
     * @return null nếu thành công, thông báo lỗi nếu thất bại
     */
    public String xuLyDangNhap(String maNhanVien, String matKhau) {
        if (soLanDangNhapSai >= UIConstants.MAX_DANG_NHAP_SAI) {
            return "TÀI KHOẢN BỊ KHÓA sau " + UIConstants.MAX_DANG_NHAP_SAI + " lần sai.";
        }

        NhanVienQuanLy nv = authController.dangNhap(maNhanVien, matKhau);
        if (nv == null) {
            soLanDangNhapSai++;
            int conLai = UIConstants.MAX_DANG_NHAP_SAI - soLanDangNhapSai;
            if (soLanDangNhapSai >= UIConstants.MAX_DANG_NHAP_SAI) {
                return "Sai thông tin. Tài khoản bị khóa!";
            }
            return "Sai mã nhân viên hoặc mật khẩu. Còn " + conLai + " lần thử.";
        }

        // Đăng nhập thành công
        nhanVienHienTai  = nv;
        soLanDangNhapSai = 0;
        mainAdminPanel.khoiTaoLai();
        cardLayout.show(panelChinh, MAN_HINH_CHINH);
        return null;
    }

    /** Đăng xuất, xóa phiên, quay về màn hình đăng nhập. */
    public void xuLyDangXuat() {
        authController.dangXuat();
        nhanVienHienTai = null;
        dangNhapPanel.khoiTaoLai();
        cardLayout.show(panelChinh, MAN_HINH_DANG_NHAP);
    }

    /** Kiểm tra tài khoản có bị khóa không. */
    public boolean laBiKhoa() {
        return soLanDangNhapSai >= UIConstants.MAX_DANG_NHAP_SAI;
    }

    public NhanVienQuanLy getNhanVienHienTai() { return nhanVienHienTai; }

    /** Khởi chạy ứng dụng Admin trên Event Dispatch Thread. */
    public static void khoiChay() {
        SwingUtilities.invokeLater(() -> {
            AdminApp app = new AdminApp();
            app.setVisible(true);
        });
    }
}
