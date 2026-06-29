package util;

import model.BacSi;
import model.BenhNhan;
import model.ChuyenKhoa;
import model.PhieuKham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;
import javax.print.PrintService;


/**
 * Tiện ích in phiếu khám bệnh.
 *
 * QUAN TRỌNG – luồng đúng:
 *   1. LUÔN hiện dialog xác nhận phiếu (bệnh nhân thấy thông tin trên màn hình).
 *   2. Đồng thời gửi lệnh in đến máy in nhiệt nếu có.
 *   Phải được gọi từ EDT.
 *
 * FIX: Trước đây chỉ hiện dialog khi KHÔNG có máy in → nếu máy tính có PDF printer
 *      thì dialog không hiện, bệnh nhân không thấy thông tin phiếu.
 */
public class PrinterUtil {

    private static final DateTimeFormatter FMT_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FMT_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private PrinterUtil() {}

    /**
     * Hiện dialog phiếu khám VÀ gửi lệnh in (nếu có máy in).
     * Gọi từ EDT.
     */
    public static void inPhieu(PhieuKham phieu, BenhNhan benhNhan,
                                BacSi bacSi, ChuyenKhoa khoa,
                                Component parent) throws PrinterException {

        // Luôn hiện dialog — bệnh nhân thấy thông tin trước khi rời kiosk
        hienDialogPhieu(phieu, benhNhan, bacSi, khoa, parent);
    }

    // ─── Dialog phiếu chính ──────────────────────────────────

    /**
     * Hiện JDialog modal với đầy đủ thông tin phiếu.
     * Có nút "In phiếu" để gửi đến máy in nhiệt nếu có.
     */
    private static void hienDialogPhieu(PhieuKham phieu, BenhNhan benhNhan,
                                         BacSi bacSi, ChuyenKhoa khoa,
                                         Component parent) {
        Window parentWindow = parent != null
                ? SwingUtilities.getWindowAncestor(parent) : null;

        JDialog dialog = new JDialog(parentWindow,
                "Phiếu đăng ký khám bệnh",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 620);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout(0, 0));

        // ── Tiêu đề số thứ tự ────────────────────────────────
        JPanel panelSoTT = new JPanel(new GridLayout(2, 1, 0, 2));
        panelSoTT.setBackground(UIConstants.MAU_NHAN);
        panelSoTT.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblNhanSTT = new JLabel("SỐ THỨ TỰ", SwingConstants.CENTER);
        lblNhanSTT.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblNhanSTT.setForeground(new Color(0xBBDEFB));

        JLabel lblSoTT = new JLabel(String.valueOf(phieu.getSoThuTu()),
                SwingConstants.CENTER);
        lblSoTT.setFont(new Font("SansSerif", Font.BOLD, 80));
        lblSoTT.setForeground(Color.WHITE);

        panelSoTT.add(lblNhanSTT);
        panelSoTT.add(lblSoTT);

        // ── Thông tin chi tiết phiếu ─────────────────────────
        JPanel panelThongTin = new JPanel(new GridLayout(6, 2, 8, 12));
        panelThongTin.setBackground(Color.WHITE);
        panelThongTin.setBorder(new EmptyBorder(24, 30, 24, 30));

        String ngayKham = phieu.getNgayKham() != null
                ? phieu.getNgayKham().format(FMT_NGAY) : "---";
        String ngayGio  = phieu.getNgayGioLayPhieu() != null
                ? phieu.getNgayGioLayPhieu().format(FMT_NGAY_GIO) : "---";
        String tenBN    = benhNhan != null ? benhNhan.getHoTen()       : "---";
        String maBHYT   = benhNhan != null ? benhNhan.getMaBaoHiemYTe(): "---";
        String tenKhoa  = khoa     != null ? khoa.getTenKhoa()         : "---";
        String tenBS    = bacSi    != null ? bacSi.getTenBacSi()       : "---";

        them2Cot(panelThongTin, "Họ tên bệnh nhân:",   tenBN);
        them2Cot(panelThongTin, "Mã BHYT:",             maBHYT);
        them2Cot(panelThongTin, "Chuyên khoa:",         tenKhoa);
        them2Cot(panelThongTin, "Bác sĩ phụ trách:",   tenBS);
        them2Cot(panelThongTin, "Ngày khám:",           ngayKham);
        them2Cot(panelThongTin, "Giờ lấy số:",          ngayGio);

        // ── Ghi chú ──────────────────────────────────────────
        JLabel lblGhiChu = new JLabel(
                "Vui lòng giữ phiếu và chờ được gọi số!", SwingConstants.CENTER);
        lblGhiChu.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblGhiChu.setForeground(new Color(0x555555));
        lblGhiChu.setBorder(new EmptyBorder(0, 0, 10, 0));

        // ── Nút bấm ──────────────────────────────────────────
        JPanel panelNut = new JPanel(new GridLayout(1, 2, 10, 0));
        panelNut.setBackground(Color.WHITE);
        panelNut.setBorder(new EmptyBorder(0, 30, 20, 30));

        JButton btnIn   = new JButton("🖨  Gửi máy in");
        JButton btnDong = new JButton("✓  Đóng phiếu");

        String finalNgayKham = ngayKham;
        String finalTenBN    = tenBN;
        String finalMaBHYT   = maBHYT;
        String finalTenKhoa  = tenKhoa;
        String finalTenBS    = tenBS;
        String finalNgayGio  = ngayGio;

        btnIn.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnIn.setBackground(new Color(0xFF8F00));
        btnIn.setForeground(Color.WHITE);
        btnIn.setFocusPainted(false);
        btnIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIn.addActionListener(e -> {
            String content = xayDungNguyenVanPhieu(
                    phieu.getSoThuTu(), finalTenBN, finalMaBHYT,
                    finalTenKhoa, finalTenBS, finalNgayKham, finalNgayGio);
            boolean daIn = thuInMayIn(content);
            if (daIn) {
                JOptionPane.showMessageDialog(dialog,
                        "Đã gửi lệnh in thành công!", "In phiếu",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Không tìm thấy máy in hoặc lỗi in.\nVui lòng ghi lại số thứ tự: "
                        + phieu.getSoThuTu(), "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDong.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnDong.setBackground(UIConstants.MAU_THANH_CONG);
        btnDong.setForeground(Color.WHITE);
        btnDong.setFocusPainted(false);
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> dialog.dispose());

        panelNut.add(btnIn);
        panelNut.add(btnDong);

        // ── Lắp ráp dialog ───────────────────────────────────
        JPanel panelGiua = new JPanel(new BorderLayout());
        panelGiua.setBackground(Color.WHITE);
        panelGiua.add(panelThongTin, BorderLayout.CENTER);
        panelGiua.add(lblGhiChu,     BorderLayout.SOUTH);

        dialog.add(panelSoTT,   BorderLayout.NORTH);
        dialog.add(panelGiua,   BorderLayout.CENTER);
        dialog.add(panelNut,    BorderLayout.SOUTH);

        dialog.setVisible(true); // block EDT cho đến khi người dùng đóng
    }

    // ─── helpers ─────────────────────────────────────────────

    /** Thêm 1 hàng nhãn + giá trị vào panel 2 cột. */
    private static void them2Cot(JPanel panel, String nhan, String giaTri) {
        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNhan.setForeground(new Color(0x555555));

        JLabel lblGia = new JLabel(giaTri);
        lblGia.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblGia.setForeground(UIConstants.MAU_CHU);

        panel.add(lblNhan);
        panel.add(lblGia);
    }

    /** Xây dựng nội dung text thuần để gửi đến máy in nhiệt. */
    private static String xayDungNguyenVanPhieu(int soThuTu, String tenBN,
                                                  String maBHYT, String tenKhoa,
                                                  String tenBS, String ngayKham,
                                                  String ngayGio) {
        return "===================================\n"
             + "    PHIEU DANG KY KHAM BENH\n"
             + "===================================\n\n"
             + "  SO THU TU : " + soThuTu + "\n\n"
             + "-----------------------------------\n"
             + "  Ho ten   : " + tenBN    + "\n"
             + "  Ma BHYT  : " + maBHYT   + "\n"
             + "-----------------------------------\n"
             + "  Khoa     : " + tenKhoa  + "\n"
             + "  Bac si   : " + tenBS    + "\n"
             + "-----------------------------------\n"
             + "  Ngay kham: " + ngayKham + "\n"
             + "  Gio lay so: " + ngayGio  + "\n"
             + "===================================\n"
             + "   Vui long giu phieu, cho goi so\n"
             + "===================================\n";
    }

    /**
     * Gửi lệnh in đến máy in mặc định.
     * @return true nếu gửi thành công
     */
    private static boolean thuInMayIn(String noiDung) {
        PrintService[] ds = PrinterJob.lookupPrintServices();
        if (ds == null || ds.length == 0) return false;
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((g, pf, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                g2.setColor(Color.BLACK);
                float x = (float) pf.getImageableX() + 5;
                float y = (float) pf.getImageableY() + 20;
                for (String dong : noiDung.split("\n")) {
                    g2.drawString(dong, x, y);
                    y += 15;
                }
                return Printable.PAGE_EXISTS;
            });
            job.print();
            return true;
        } catch (PrinterException e) {
            System.err.println("[PrinterUtil] Lỗi in: " + e.getMessage());
            return false;
        }
    }
}
