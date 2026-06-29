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

    private static String layCheDo(String[] args) {
        if (args != null && args.length > 0) {
            return args[0].trim();
        }
        return "";
    }

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
     * Dùng Nimbus L&F — hiển thị đúng màu setBackground() trên mọi hệ điều hành.
     * KHÔNG dùng getSystemLookAndFeelClassName() vì macOS Aqua và Windows L&F
     * tự vẽ lại nút theo kiểu native, bỏ qua setBackground() hoàn toàn.
     */
    private static void datLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());

                    // Ghi đè màu nền nút mặc định của Nimbus
                    UIManager.getLookAndFeelDefaults().put("Button.background",
                            new javax.swing.plaf.ColorUIResource(0xEAEEF7));

                    return;
                }
            }
            // Nimbus không có → dùng Cross-platform (Metal) thay thế
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Không thể đặt Look-and-Feel: " + e.getMessage());
        }
    }
}