package view.admin;

import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel đăng nhập phân hệ Admin (UC07).
 * Giới hạn MAX_DANG_NHAP_SAI = 3 lần thử, sau đó khóa nút đăng nhập.
 */
public class DangNhapPanel extends JPanel {

    private final AdminApp adminApp;

    // ─── Thành phần giao diện ────────────────────────────────
    private JTextField     txtMaNhanVien;
    private JPasswordField txtMatKhau;
    private JButton        btnDangNhap;
    private JLabel         lblThongBaoLoi;

    public DangNhapPanel(AdminApp adminApp) {
        this.adminApp = adminApp;
        khoiTaoGiaoDien();
    }

    /** Reset lại form về trạng thái ban đầu. */
    public void khoiTaoLai() {
        txtMaNhanVien.setText("");
        txtMatKhau.setText("");
        lblThongBaoLoi.setVisible(false);
        btnDangNhap.setEnabled(!adminApp.laBiKhoa());
    }

    // ─── Khởi tạo giao diện ──────────────────────────────────

    private void khoiTaoGiaoDien() {
        setLayout(new GridBagLayout());
        setBackground(UIConstants.MAU_NEN);
        add(taoCardDangNhap());
    }

    private JPanel taoCardDangNhap() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 1),
                new EmptyBorder(40, 50, 40, 50)));
        card.setPreferredSize(new Dimension(420, 420));

        card.add(taoTieuDe());
        card.add(Box.createVerticalStrut(30));
        card.add(taoTruongMaNhanVien());
        card.add(Box.createVerticalStrut(15));
        card.add(taoTruongMatKhau());
        card.add(Box.createVerticalStrut(10));
        card.add(taoLabelLoi());
        card.add(Box.createVerticalStrut(20));
        card.add(taoNutDangNhap());
        return card;
    }

    private JLabel taoTieuDe() {
        JLabel lbl = new JLabel("ĐĂNG NHẬP QUẢN TRỊ", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        lbl.setForeground(UIConstants.MAU_NHAN);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel taoTruongMaNhanVien() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Mã nhân viên:");
        lbl.setFont(UIConstants.FONT_ADMIN_BOLD);
        lbl.setForeground(UIConstants.MAU_CHU);

        txtMaNhanVien = new JTextField();
        txtMaNhanVien.setFont(UIConstants.FONT_ADMIN);
        txtMaNhanVien.setPreferredSize(new Dimension(320, 38));
        txtMaNhanVien.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtMaNhanVien.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtMatKhau.requestFocus();
            }
        });

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(txtMaNhanVien, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoTruongMatKhau() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Mật khẩu:");
        lbl.setFont(UIConstants.FONT_ADMIN_BOLD);
        lbl.setForeground(UIConstants.MAU_CHU);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(UIConstants.FONT_ADMIN);
        txtMatKhau.setPreferredSize(new Dimension(320, 38));
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) xuLyDangNhap();
            }
        });

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(txtMatKhau, BorderLayout.CENTER);
        return panel;
    }

    private JLabel taoLabelLoi() {
        lblThongBaoLoi = new JLabel("", SwingConstants.CENTER);
        lblThongBaoLoi.setFont(UIConstants.FONT_ADMIN_BOLD);
        lblThongBaoLoi.setForeground(UIConstants.MAU_LOI);
        lblThongBaoLoi.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblThongBaoLoi.setVisible(false);
        return lblThongBaoLoi;
    }

    private JButton taoNutDangNhap() {
        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnDangNhap.setPreferredSize(new Dimension(320, 42));
        btnDangNhap.setMaximumSize(new Dimension(320, 42));
        btnDangNhap.setBackground(UIConstants.MAU_NHAN);
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        return btnDangNhap;
    }

    // ─── Xử lý sự kiện ───────────────────────────────────────

    private void xuLyDangNhap() {
        String maNhanVien = txtMaNhanVien.getText().trim();
        String matKhau    = new String(txtMatKhau.getPassword());

        String loi = adminApp.xuLyDangNhap(maNhanVien, matKhau);
        if (loi != null) {
            lblThongBaoLoi.setText(loi);
            lblThongBaoLoi.setVisible(true);
            txtMatKhau.setText("");
            if (adminApp.laBiKhoa()) {
                btnDangNhap.setEnabled(false);
            }
        }
    }
}
