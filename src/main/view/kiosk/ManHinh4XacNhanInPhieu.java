package view.kiosk;

import controller.KioskController;
import model.BacSi;
import model.BenhNhan;
import model.ChuyenKhoa;
import model.PhieuKham;
import util.PrinterUtil;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;

/**
 * Màn hình 4 – Xác nhận thông tin và in phiếu khám (UC05, UC06).
 *
 * Luồng đúng (fix threading):
 *   1. doInBackground()  → gọi controller.dangKy() (DB, background thread)
 *   2. done()            → gọi PrinterUtil.inPhieu() (dialog/máy in, EDT)
 *   3. done()            → hiển thị số thứ tự, đặt Timer reset 5 giây
 */
public class ManHinh4XacNhanInPhieu extends JPanel {

    private final KioskApp        kioskApp;
    private final KioskController controller = new KioskController();

    private JLabel  lblHoTen;
    private JLabel  lblKhoa;
    private JLabel  lblBacSi;
    private JLabel  lblSoThuTu;
    private JLabel  lblThongBaoLoi;
    private JButton btnInPhieu;
    private JPanel  panelSoThuTu;

    public ManHinh4XacNhanInPhieu(KioskApp kioskApp) {
        this.kioskApp = kioskApp;
        khoiTaoGiaoDien();
    }

    public void khoiTaoLai() {
        BenhNhan   bn = kioskApp.getBenhNhanHienTai();
        ChuyenKhoa ck = kioskApp.getChuyenKhoaHienTai();
        BacSi      bs = kioskApp.getBacSiHienTai();

        lblHoTen.setText("Họ tên bệnh nhân : " + (bn != null ? bn.getHoTen() : "---"));
        lblKhoa .setText("Chuyên khoa       : " + (ck != null ? ck.getTenKhoa() : "---"));
        lblBacSi.setText("Bác sĩ phụ trách  : " + (bs != null ? bs.getTenBacSi() : "---"));

        panelSoThuTu.setVisible(false);
        lblSoThuTu.setText("");
        lblThongBaoLoi.setVisible(false);
        btnInPhieu.setEnabled(true);
    }

    // ─── Giao diện ───────────────────────────────────────────

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UIConstants.MAU_NEN);
        setBorder(new EmptyBorder(40, 80, 40, 80));
        add(taoPhanTren(), BorderLayout.NORTH);
        add(taoPhanGiua(), BorderLayout.CENTER);
        add(taoPhanDuoi(), BorderLayout.SOUTH);
    }

    private JLabel taoPhanTren() {
        JLabel lbl = new JLabel("XÁC NHẬN ĐĂNG KÝ", SwingConstants.CENTER);
        lbl.setFont(UIConstants.FONT_TIEU_DE);
        lbl.setForeground(UIConstants.MAU_NHAN);
        return lbl;
    }

    private JPanel taoPhanGiua() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Card thông tin xác nhận
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 14));
        card.setBackground(new Color(0xF5F5F5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 1),
                new EmptyBorder(28, 32, 28, 32)));

        lblHoTen = taoLblInfo("Họ tên bệnh nhân : ---");
        lblKhoa  = taoLblInfo("Chuyên khoa       : ---");
        lblBacSi = taoLblInfo("Bác sĩ phụ trách  : ---");
        card.add(lblHoTen);
        card.add(lblKhoa);
        card.add(lblBacSi);

        panelSoThuTu = taoPanelSoThuTu();
        panelSoThuTu.setVisible(false);

        lblThongBaoLoi = new JLabel("", SwingConstants.CENTER);
        lblThongBaoLoi.setFont(UIConstants.FONT_ADMIN_BOLD);
        lblThongBaoLoi.setForeground(UIConstants.MAU_LOI);
        lblThongBaoLoi.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblThongBaoLoi.setVisible(false);

        panel.add(card);
        panel.add(Box.createVerticalStrut(20));
        panel.add(panelSoThuTu);
        panel.add(lblThongBaoLoi);
        return panel;
    }

    private JLabel taoLblInfo(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 19));
        l.setForeground(UIConstants.MAU_CHU);
        return l;
    }

    private JPanel taoPanelSoThuTu() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lblNhan = new JLabel("SỐ THỨ TỰ CỦA BẠN", SwingConstants.CENTER);
        lblNhan.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNhan.setForeground(UIConstants.MAU_THANH_CONG);

        lblSoThuTu = new JLabel("", SwingConstants.CENTER);
        lblSoThuTu.setFont(UIConstants.FONT_BANG_SO);
        lblSoThuTu.setForeground(UIConstants.MAU_NHAN);

        JLabel lblGhiChu = new JLabel("Vui lòng giữ phiếu và chờ gọi số!", SwingConstants.CENTER);
        lblGhiChu.setFont(UIConstants.FONT_KIOSK);
        lblGhiChu.setForeground(UIConstants.MAU_CHU);

        p.add(lblNhan,    BorderLayout.NORTH);
        p.add(lblSoThuTu, BorderLayout.CENTER);
        p.add(lblGhiChu,  BorderLayout.SOUTH);
        return p;
    }

    private JPanel taoPhanDuoi() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p.setOpaque(false);

        JButton btnQuayLai = new JButton("← QUAY LẠI");
        btnQuayLai.setFont(UIConstants.FONT_NUT);
        btnQuayLai.setPreferredSize(new Dimension(200, UIConstants.NUT_CAO_MIN));
        btnQuayLai.setBackground(UIConstants.MAU_BORDER);
        btnQuayLai.setForeground(UIConstants.MAU_CHU);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> kioskApp.chuyenManHinh(KioskApp.MAN_HINH_CHON_BAC_SI));

        btnInPhieu = new JButton("🖨  IN PHIẾU");
        btnInPhieu.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnInPhieu.setPreferredSize(new Dimension(260, UIConstants.NUT_CAO_MIN + 20));
        btnInPhieu.setBackground(UIConstants.MAU_NHAN);
        btnInPhieu.setForeground(Color.WHITE);
        btnInPhieu.setFocusPainted(false);
        btnInPhieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnInPhieu.addActionListener(e -> xuLyInPhieu());

        p.add(btnQuayLai);
        p.add(btnInPhieu);
        return p;
    }

    // ─── Luồng xử lý in phiếu (đã fix threading) ────────────

    private void xuLyInPhieu() {
        BenhNhan   bn = kioskApp.getBenhNhanHienTai();
        ChuyenKhoa ck = kioskApp.getChuyenKhoaHienTai();
        BacSi      bs = kioskApp.getBacSiHienTai();

        if (bn == null || ck == null || bs == null) {
            hienLoi("Thiếu thông tin đăng ký. Vui lòng bắt đầu lại.");
            return;
        }

        btnInPhieu.setEnabled(false);
        lblThongBaoLoi.setVisible(false);

        /**
         * Bước 1 – doInBackground (background thread):
         *   Chỉ thao tác DB: tạo hàng chờ, tăng số thứ tự, lưu phiếu.
         *   KHÔNG gọi PrinterUtil ở đây vì Swing dialog phải chạy trên EDT.
         */
        SwingWorker<PhieuKham, Void> worker = new SwingWorker<>() {
            @Override
            protected PhieuKham doInBackground() {
                return controller.dangKy(bn, bs, ck);
            }

            /**
             * Bước 2 – done() chạy trên EDT:
             *   Gọi PrinterUtil (an toàn với Swing), hiển thị số thứ tự, đặt timer reset.
             */
            @Override
            protected void done() {
                try {
                    PhieuKham phieu = get();
                    // In phiếu TRÊN EDT — dialog sẽ hiện đầy đủ thông tin
                    try {
                        PrinterUtil.inPhieu(phieu, bn, bs, ck, ManHinh4XacNhanInPhieu.this);
                    } catch (PrinterException pe) {
                        System.err.println("[ManHinh4] Lỗi máy in: " + pe.getMessage());
                        // Không chặn: số thứ tự vẫn đã lưu DB, tiếp tục hiển thị
                    }
                    hienSoThuTuVaReset(phieu.getSoThuTu());

                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    hienLoi("Lỗi: " + cause.getMessage());
                    btnInPhieu.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    /** Hiển thị số thứ tự to, sau KIOSK_RESET_DELAY_MS tự quay về màn hình 1. */
    private void hienSoThuTuVaReset(int soThuTu) {
        lblSoThuTu.setText(String.valueOf(soThuTu));
        panelSoThuTu.setVisible(true);
        btnInPhieu.setEnabled(false);

        Timer t = new Timer(UIConstants.KIOSK_RESET_DELAY_MS, e -> kioskApp.resetPhien());
        t.setRepeats(false);
        t.start();
    }

    private void hienLoi(String msg) {
        lblThongBaoLoi.setText(msg);
        lblThongBaoLoi.setVisible(true);
    }
}
