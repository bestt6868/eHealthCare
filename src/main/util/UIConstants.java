package util;

import java.awt.Color;
import java.awt.Font;

/**
 * Hằng số giao diện dùng chung cho toàn bộ ứng dụng.
 * Thay đổi tại đây sẽ áp dụng cho tất cả các màn hình.
 */
public class UIConstants {

    // ─── Màu sắc ─────────────────────────────────────────────
    public static final Color MAU_NEN        = Color.WHITE;
    public static final Color MAU_NHAN       = new Color(0x1976D2);   // xanh dương Material
    public static final Color MAU_CHU        = new Color(0x212121);   // đen đậm
    public static final Color MAU_LOI        = new Color(0xD32F2F);   // đỏ Material
    public static final Color MAU_THANH_CONG = new Color(0x388E3C);   // xanh lá Material
    public static final Color MAU_NUT_HOVER  = new Color(0x1565C0);   // xanh đậm hơn khi hover
    public static final Color MAU_NEN_SIDEBAR= new Color(0xECEFF1);   // xám nhạt sidebar
    public static final Color MAU_BORDER     = new Color(0xBDBDBD);   // viền xám
    public static final Color MAU_TIEU_DE_PANEL = new Color(0x0D47A1); // xanh đậm tiêu đề

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
    public static final int KIOSK_RESET_DELAY_MS = 10000;   // 10 giây sau khi in phiếu

    // Constructor private – không cho phép tạo instance
    private UIConstants() {}
}
