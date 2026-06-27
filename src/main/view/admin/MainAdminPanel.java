package view.admin;

import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel chính sau đăng nhập.
 * Sidebar menu bên trái, vùng nội dung bên phải dùng CardLayout.
 * Hiển thị tên nhân viên và nút đăng xuất ở thanh trên.
 */
public class MainAdminPanel extends JPanel {

    private static final String TAB_KHOA     = "KHOA";
    private static final String TAB_BAC_SI   = "BAC_SI";
    private static final String TAB_CA_TRUC  = "CA_TRUC";
    private static final String TAB_HANG_CHO = "HANG_CHO";

    private final AdminApp adminApp;

    // ─── Thành phần giao diện ────────────────────────────────
    private JLabel         lblTenNhanVien;
    private CardLayout     contentCardLayout;
    private JPanel         panelContent;
    private JButton        btnDangXuat;

    // ─── Các sub-panel quản lý ───────────────────────────────
    private QuanLyKhoaPanel     quanLyKhoaPanel;
    private QuanLyBacSiPanel    quanLyBacSiPanel;
    private QuanLyCaTrucPanel   quanLyCaTrucPanel;
    private QuanLyHangChoPanel  quanLyHangChoPanel;

    public MainAdminPanel(AdminApp adminApp) {
        this.adminApp = adminApp;
        khoiTaoGiaoDien();
    }

    /** Cập nhật tên nhân viên và refresh panel mặc định khi đăng nhập. */
    public void khoiTaoLai() {
        if (adminApp.getNhanVienHienTai() != null) {
            lblTenNhanVien.setText("👤  " + adminApp.getNhanVienHienTai().getHoTenNhanVien());
        }
        // Hiện tab khoa mặc định và refresh
        chuyenTab(TAB_KHOA);
        quanLyKhoaPanel.taiDuLieu();
    }

    // ─── Khởi tạo giao diện ──────────────────────────────────

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.MAU_NEN);

        add(taoThanhTren(), BorderLayout.NORTH);
        add(taoSidebar(),   BorderLayout.WEST);
        add(taoVungContent(), BorderLayout.CENTER);
    }

    /** Thanh tiêu đề trên cùng: tên hệ thống + tên nhân viên + nút đăng xuất. */
    private JPanel taoThanhTren() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.MAU_TIEU_DE_PANEL);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setPreferredSize(new Dimension(0, 55));

        JLabel lblTen = new JLabel("🏥  Hệ thống Quản trị Phiếu Khám");
        lblTen.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTen.setForeground(Color.WHITE);

        JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelRight.setOpaque(false);

        lblTenNhanVien = new JLabel("---");
        lblTenNhanVien.setFont(UIConstants.FONT_ADMIN);
        lblTenNhanVien.setForeground(Color.WHITE);

        btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnDangXuat.setBackground(UIConstants.MAU_LOI);
        btnDangXuat.setForeground(Color.WHITE);
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDangXuat.addActionListener(e -> adminApp.xuLyDangXuat());

        panelRight.add(lblTenNhanVien);
        panelRight.add(btnDangXuat);

        panel.add(lblTen,       BorderLayout.WEST);
        panel.add(panelRight,   BorderLayout.EAST);
        return panel;
    }

    /** Sidebar menu bên trái với 4 nút điều hướng. */
    private JPanel taoSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.MAU_NEN_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));
        sidebar.setPreferredSize(new Dimension(220, 0));

        String[][] menu = {
            {"🏥  Quản lý Khoa",      TAB_KHOA},
            {"👨‍⚕️  Quản lý Bác sĩ",    TAB_BAC_SI},
            {"📅  Quản lý Ca trực",    TAB_CA_TRUC},
            {"🔢  Quản lý Hàng chờ",   TAB_HANG_CHO}
        };

        for (String[] item : menu) {
            sidebar.add(taoNutMenu(item[0], item[1]));
            sidebar.add(Box.createVerticalStrut(5));
        }
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton taoNutMenu(String nhan, String tab) {
        JButton nut = new JButton(nhan);
        nut.setFont(UIConstants.FONT_ADMIN_BOLD);
        nut.setMaximumSize(new Dimension(220, 50));
        nut.setPreferredSize(new Dimension(220, 50));
        nut.setBackground(UIConstants.MAU_NEN_SIDEBAR);
        nut.setForeground(UIConstants.MAU_CHU);
        nut.setHorizontalAlignment(SwingConstants.LEFT);
        nut.setBorder(new EmptyBorder(0, 20, 0, 10));
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.addActionListener(e -> chuyenTab(tab));
        return nut;
    }

    /** Vùng nội dung bên phải với CardLayout chứa 4 panel quản lý. */
    private JPanel taoVungContent() {
        contentCardLayout = new CardLayout();
        panelContent      = new JPanel(contentCardLayout);
        panelContent.setBackground(UIConstants.MAU_NEN);

        quanLyKhoaPanel    = new QuanLyKhoaPanel(adminApp);
        quanLyBacSiPanel   = new QuanLyBacSiPanel(adminApp);
        quanLyCaTrucPanel  = new QuanLyCaTrucPanel(adminApp);
        quanLyHangChoPanel = new QuanLyHangChoPanel(adminApp);

        panelContent.add(quanLyKhoaPanel,    TAB_KHOA);
        panelContent.add(quanLyBacSiPanel,   TAB_BAC_SI);
        panelContent.add(quanLyCaTrucPanel,  TAB_CA_TRUC);
        panelContent.add(quanLyHangChoPanel, TAB_HANG_CHO);

        return panelContent;
    }

    private void chuyenTab(String tab) {
        contentCardLayout.show(panelContent, tab);
        // Refresh dữ liệu khi chuyển tab
        switch (tab) {
            case TAB_BAC_SI:   quanLyBacSiPanel.taiDuLieu();   break;
            case TAB_CA_TRUC:  quanLyCaTrucPanel.taiDuLieu();  break;
            case TAB_HANG_CHO: quanLyHangChoPanel.taiDuLieu(); break;
        }
    }
}
