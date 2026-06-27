import view.admin.AdminApp;
import view.kiosk.KioskApp;

import javax.swing.*;

/**
 * Entry point của ứng dụng HospitalKiosk.
 * Chạy với tham số dòng lệnh:
 *   java Main kiosk  → khởi động phân hệ Kiosk (bệnh nhân)
 *   java Main admin  → khởi động phân hệ Quản trị (nhân viên)
 *   java Main        → hiển thị dialog chọn
 */
public class Main {

    public static void main(String[] args) {
        // Dùng look-and-feel hệ điều hành để giao diện đẹp hơn
        datLookAndFeel();

        String cheDoChay = layCheDo(args);

        switch (cheDoChay.toLowerCase()) {
            case "kiosk":
                System.out.println("Khởi động phân hệ KIOSK...");
                KioskApp.khoiChay();
                break;
            case "admin":
                System.out.println("Khởi động phân hệ QUẢN TRỊ...");
                AdminApp.khoiChay();
                break;
            default:
                hienDialogChon();
                break;
        }
    }

    /**
     * Lấy chế độ chạy từ tham số dòng lệnh.
     * Nếu không có tham số → trả về chuỗi rỗng (hiện dialog chọn).
     */
    private static String layCheDo(String[] args) {
        if (args != null && args.length > 0) {
            return args[0].trim();
        }
        return "";
    }

    /**
     * Hiển thị dialog để chọn phân hệ khi không có tham số dòng lệnh.
     */
    private static void hienDialogChon() {
        SwingUtilities.invokeLater(() -> {
            String[] luaChon = {"🖥  Kiosk (Bệnh nhân)", "🔧  Quản trị (Nhân viên)", "Thoát"};
            int chon = JOptionPane.showOptionDialog(
                    null,
                    "Chọn phân hệ cần khởi động:",
                    "Hospital Kiosk – Chọn phân hệ",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    luaChon,
                    luaChon[0]
            );

            switch (chon) {
                case 0: KioskApp.khoiChay(); break;
                case 1: AdminApp.khoiChay(); break;
                default: System.exit(0);
            }
        });
    }

    /**
     * Đặt Look-and-Feel phù hợp với hệ điều hành hiện tại.
     */
    private static void datLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Giữ nguyên look-and-feel mặc định nếu không thể thay đổi
            System.err.println("Không thể đặt Look-and-Feel hệ thống: " + e.getMessage());
        }
    }
}
