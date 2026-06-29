package util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
/**
 * Hằng số giao diện dùng chung cho toàn bộ ứng dụng.
 * Thay đổi tại đây sẽ áp dụng cho tất cả các màn hình.
 */
/**public class UIConstants {

    // ─── Màu sắc ─────────────────────────────────────────────
    public static final Color MAU_NEN        = Color.WHITE;
    public static final Color MAU_NHAN       = new Color(0x3D5A99);
    public static final Color MAU_CHU        = new Color(0x1A2540);   
    public static final Color MAU_LOI        = new Color(0xC53030);   
    public static final Color MAU_THANH_CONG = new Color(0x2E8B57);   
    public static final Color MAU_NUT_HOVER  = new Color(0x2A3F6B);    
    public static final Color MAU_NEN_SIDEBAR= new Color(0xEAEEF7);   
    public static final Color MAU_BORDER     = new Color(0xBEC8E0);   
    public static final Color MAU_TIEU_DE_PANEL = new Color(0x1A2540);

    // ─── Font chữ ────────────────────────────────────────────
    public static final Font FONT_KIOSK    = new Font("SansSerif", Font.PLAIN, 20);
    public static final Font FONT_TIEU_DE  = new Font("SansSerif", Font.BOLD,  28);
    public static final Font FONT_NUT      = new Font("SansSerif", Font.BOLD,  18);
    public static final Font FONT_BANG_SO  = new Font("SansSerif", Font.BOLD,  80);  // số thứ tự to
    public static final Font FONT_ADMIN    = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_ADMIN_BOLD = new Font("SansSerif", Font.BOLD, 14);

    // ─── Kích thước nút ──────────────────────────────────────
    public static final int NUT_RONG_MIN    = 80;
    public static final int NUT_CAO_MIN     = 60;
    public static final int NUT_BHYT_RONG   = 100;
    public static final int NUT_BHYT_CAO    = 80;
    public static final int NUT_KHOA_RONG   = 220;
    public static final int NUT_KHOA_CAO    = 80;
    public static final int KHOANG_CACH_NUT = 10;

    // ─── Giới hạn nghiệp vụ ──────────────────────────────────
    public static final int MAX_DANG_NHAP_SAI = 3;
    public static final int MA_BHYT_LENGTH    = 15;

    // ─── Timeout tự động reset kiosk (mili giây) ────────────
    public static final int KIOSK_RESET_DELAY_MS = 5000;   // 5 giây sau khi in phiếu
    // Constructor private – không cho phép tạo instance
    private UIConstants() {}
}
**/


public class UIConstants {

    // ─── Màu sắc ─────────────────────────────────────────────
    public static final Color MAU_NEN           = new Color(0xF4F6FB);
    public static final Color MAU_NHAN          = new Color(0x3D5A99);   // Blue Dusk – nút chính
    public static final Color MAU_CHU           = new Color(0x1A2540);   // đen đậm navy
    public static final Color MAU_LOI           = new Color(0xC53030);   // đỏ
    public static final Color MAU_THANH_CONG    = new Color(0x2E8B57);   // xanh lá
    public static final Color MAU_NUT_HOVER     = new Color(0x2A3F6B);   // xanh đậm hơn khi hover
    public static final Color MAU_NEN_SIDEBAR   = new Color(0xEAEEF7);   // xanh nhạt sidebar
    public static final Color MAU_BORDER        = new Color(0xBEC8E0);   // viền xanh nhạt
    public static final Color MAU_TIEU_DE_PANEL = new Color(0x1A2540);   // navy đậm tiêu đề
    public static final Color MAU_TRANG         = Color.WHITE;           // trắng thuần (card)

    // ─── Font chữ ────────────────────────────────────────────
    public static final Font FONT_KIOSK      = new Font("SansSerif", Font.PLAIN, 20);
    public static final Font FONT_TIEU_DE    = new Font("SansSerif", Font.BOLD,  28);
    public static final Font FONT_NUT        = new Font("SansSerif", Font.BOLD,  18);
    public static final Font FONT_BANG_SO    = new Font("SansSerif", Font.BOLD,  80);
    public static final Font FONT_ADMIN      = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_ADMIN_BOLD = new Font("SansSerif", Font.BOLD,  14);

    // ─── Kích thước nút ──────────────────────────────────────
    public static final int NUT_RONG_MIN    = 80;
    public static final int NUT_CAO_MIN     = 60;
    public static final int NUT_BHYT_RONG   = 100;
    public static final int NUT_BHYT_CAO    = 80;
    public static final int NUT_KHOA_RONG   = 220;
    public static final int NUT_KHOA_CAO    = 80;
    public static final int KHOANG_CACH_NUT = 10;

    // ─── Giới hạn nghiệp vụ ──────────────────────────────────
    public static final int MAX_DANG_NHAP_SAI = 3;
    public static final int MA_BHYT_LENGTH    = 15;

    // ─── Timeout tự động reset kiosk (mili giây) ────────────
    public static final int KIOSK_RESET_DELAY_MS = 5000;

    // ─── Factory tạo nút đã được style sẵn ──────────────────
    /**
     * Tạo nút chính (nền màu, chữ trắng).
     * Dùng cho: Đăng nhập, Lưu, In phiếu, Xác nhận...
     */
    public static JButton taoNutChinh(String nhan, Color mauNen) {
        JButton nut = new JButton(nhan);
        nut.setFont(FONT_ADMIN_BOLD);
        nut.setBackground(mauNen);
        nut.setForeground(Color.WHITE);
        nut.setOpaque(true);                          // bắt buộc để Swing vẽ nền
        nut.setContentAreaFilled(true);               // bắt buộc để màu nền hiển thị
        nut.setBorderPainted(false);                  // ẩn viền mặc định của Swing
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return nut;
    }

    /**
     * Tạo nút phụ (nền nhạt, chữ tối) — dùng cho Hủy, Quay lại...
     */
    public static JButton taoNutPhu(String nhan) {
        JButton nut = new JButton(nhan);
        nut.setFont(FONT_ADMIN_BOLD);
        nut.setBackground(MAU_NEN_SIDEBAR);
        nut.setForeground(MAU_CHU);
        nut.setOpaque(true);
        nut.setContentAreaFilled(true);
        nut.setBorderPainted(true);
        nut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_BORDER, 1),
            BorderFactory.createEmptyBorder(7, 17, 7, 17)
        ));
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return nut;
    }

    /**
     * Shortcut cho nút chính màu MAU_NHAN (xanh navy).
     */
    public static JButton taoNutChinh(String nhan) {
        return taoNutChinh(nhan, MAU_NHAN);
    }

    // Constructor private – không cho phép tạo instance
    private UIConstants() {}
}