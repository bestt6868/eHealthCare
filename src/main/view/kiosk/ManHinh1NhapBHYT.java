package view.kiosk;

import controller.KioskController;
import model.BenhNhan;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Màn hình 1 – Nhập mã BHYT (UC01).
 * Bàn phím số ảo, kiểm tra 15 ký tự, xác nhận và chuyển màn hình 2.
 */
public class ManHinh1NhapBHYT extends JPanel {

    private static final int SO_NUT_HANG = 5;

    private final KioskApp        kioskApp;
    private final KioskController controller = new KioskController();

    // ─── Thành phần giao diện ────────────────────────────────
    private JLabel    lblTieuDe;
    private JLabel    lblHuongDan;
    private JTextField txtMaBHYT;
    private JLabel    lblThongBaoLoi;
    private JButton   btnXacNhan;

    private StringBuilder maBHYTDangNhap = new StringBuilder();

    public ManHinh1NhapBHYT(KioskApp kioskApp) {
        this.kioskApp = kioskApp;
        khoiTaoGiaoDien();
    }

    /** Reset lại màn hình về trạng thái ban đầu. */
    public void khoiTaoLai() {
        maBHYTDangNhap.setLength(0);
        txtMaBHYT.setText("");
        lblThongBaoLoi.setVisible(false);
        capNhatTrangThaiNutXacNhan();
    }

    // ─── Khởi tạo giao diện ──────────────────────────────────

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UIConstants.MAU_NEN);
        setBorder(new EmptyBorder(40, 60, 40, 60));

        add(taoPhiaTopTitle(), BorderLayout.NORTH);
        add(taoPhanTrungTam(), BorderLayout.CENTER);
    }

    private JPanel taoPhiaTopTitle() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        lblTieuDe = new JLabel("ĐĂNG KÝ KHÁM BỆNH", SwingConstants.CENTER);
        lblTieuDe.setFont(UIConstants.FONT_TIEU_DE);
        lblTieuDe.setForeground(UIConstants.MAU_NHAN);
        panel.add(lblTieuDe, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPhanTrungTam() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(Box.createVerticalStrut(20));
        panel.add(taoHuongDan());
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoONhapBHYT());
        panel.add(Box.createVerticalStrut(10));
        panel.add(taoLabelLoi());
        panel.add(Box.createVerticalStrut(20));
        panel.add(taoBanPhimSo());
        panel.add(Box.createVerticalStrut(20));
        panel.add(taoNutXacNhan());
        return panel;
    }

    private JLabel taoHuongDan() {
        lblHuongDan = new JLabel("Vui lòng nhập mã bảo hiểm y tế (15 ký tự):",
                SwingConstants.CENTER);
        lblHuongDan.setFont(UIConstants.FONT_KIOSK);
        lblHuongDan.setForeground(UIConstants.MAU_CHU);
        lblHuongDan.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lblHuongDan;
    }

    private JTextField taoONhapBHYT() {
        txtMaBHYT = new JTextField(20);
        txtMaBHYT.setFont(new Font("Monospaced", Font.BOLD, 28));
        txtMaBHYT.setEditable(false);
        txtMaBHYT.setHorizontalAlignment(JTextField.CENTER);
        txtMaBHYT.setMaximumSize(new Dimension(500, 60));
        txtMaBHYT.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return txtMaBHYT;
    }

    private JLabel taoLabelLoi() {
        lblThongBaoLoi = new JLabel("", SwingConstants.CENTER);
        lblThongBaoLoi.setFont(UIConstants.FONT_ADMIN_BOLD);
        lblThongBaoLoi.setForeground(UIConstants.MAU_LOI);
        lblThongBaoLoi.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblThongBaoLoi.setVisible(false);
        return lblThongBaoLoi;
    }

    private JPanel taoBanPhimSo() {
        JPanel panelBanPhim = new JPanel(new GridLayout(3, SO_NUT_HANG,
                UIConstants.KHOANG_CACH_NUT, UIConstants.KHOANG_CACH_NUT));
        panelBanPhim.setOpaque(false);
        panelBanPhim.setMaximumSize(new Dimension(600, 280));

        // Hàng 1: 1-5, Hàng 2: 6-0, Hàng 3: XÓA KÝ TỰ, XÓA HẾT
        String[] cacNhan = {"1","2","3","4","5","6","7","8","9","0","⌫","✕"};
        for (String nhan : cacNhan) {
            panelBanPhim.add(taoNutBanPhim(nhan));
        }
        return panelBanPhim;
    }

    private JButton taoNutBanPhim(String nhan) {
        JButton nut = new JButton(nhan);
        nut.setFont(UIConstants.FONT_NUT);
        nut.setPreferredSize(new Dimension(UIConstants.NUT_BHYT_RONG, UIConstants.NUT_BHYT_CAO));
        nut.setBackground(Color.WHITE);
        nut.setBorder(BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 1));
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setFocusPainted(false);
        nut.addActionListener(e -> xuLyNhapSo(nhan));
        return nut;
    }

    private JButton taoNutXacNhan() {
        btnXacNhan = new JButton("XÁC NHẬN");
        btnXacNhan.setFont(UIConstants.FONT_NUT);
        btnXacNhan.setPreferredSize(new Dimension(300, UIConstants.NUT_BHYT_CAO));
        btnXacNhan.setBackground(UIConstants.MAU_NHAN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setEnabled(false);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXacNhan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnXacNhan.addActionListener(this::xuLyXacNhan);
        return btnXacNhan;
    }

    // ─── Xử lý sự kiện ───────────────────────────────────────

    private void xuLyNhapSo(String kyTu) {
        lblThongBaoLoi.setVisible(false);
        if ("⌫".equals(kyTu)) {
            if (maBHYTDangNhap.length() > 0)
                maBHYTDangNhap.deleteCharAt(maBHYTDangNhap.length() - 1);
        } else if ("✕".equals(kyTu)) {
            maBHYTDangNhap.setLength(0);
        } else if (maBHYTDangNhap.length() < UIConstants.MA_BHYT_LENGTH) {
            maBHYTDangNhap.append(kyTu);
        }
        txtMaBHYT.setText(maBHYTDangNhap.toString());
        capNhatTrangThaiNutXacNhan();
    }

    private void capNhatTrangThaiNutXacNhan() {
        btnXacNhan.setEnabled(maBHYTDangNhap.length() == UIConstants.MA_BHYT_LENGTH);
    }

    private void xuLyXacNhan(ActionEvent e) {
        String maBHYT = maBHYTDangNhap.toString();
        try {
            BenhNhan benhNhan = controller.xacNhanBHYT(maBHYT);
            kioskApp.setBenhNhanHienTai(benhNhan);
            kioskApp.chuyenManHinh(KioskApp.MAN_HINH_CHON_KHOA);
        } catch (IllegalArgumentException ex) {
            hienThiLoi(ex.getMessage());
        }
    }

    private void hienThiLoi(String thongBao) {
        lblThongBaoLoi.setText(thongBao);
        lblThongBaoLoi.setVisible(true);
    }
}
