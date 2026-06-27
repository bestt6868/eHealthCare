package view.admin;

import controller.ChuyenKhoaController;
import model.ChuyenKhoa;
import model.NhanVienQuanLy;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý chuyên khoa (UC08, UC09, UC10).
 * FIX: hiển thị cột Mã khoa; dùng SwingWorker tải dữ liệu tránh lag.
 */
public class QuanLyKhoaPanel extends JPanel {

    private final AdminApp             adminApp;
    private final ChuyenKhoaController controller = new ChuyenKhoaController();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            lblThongBao;

    // FIX: maKhoa hiện ra ở cột đầu, không ẩn nữa
    private static final String[] COT = {"Mã khoa", "Tên khoa", "Trạng thái"};

    public QuanLyKhoaPanel(AdminApp adminApp) {
        this.adminApp = adminApp;
        khoiTaoGiaoDien();
    }

    /** Tải dữ liệu trên background thread để không lag UI. */
    public void taiDuLieu() {
        new SwingWorker<List<ChuyenKhoa>, Void>() {
            @Override protected List<ChuyenKhoa> doInBackground() {
                return controller.layTatCaKhoa();
            }
            @Override protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (ChuyenKhoa k : get()) {
                        tableModel.addRow(new Object[]{
                            k.getMaKhoa(),
                            k.getTenKhoa(),
                            k.isTrangThai() ? "✅ Hoạt động" : "🔴 Tắt"
                        });
                    }
                } catch (Exception e) {
                    hienThongBao("❌ Lỗi tải dữ liệu: " + e.getMessage(), UIConstants.MAU_LOI);
                }
            }
        }.execute();
    }

    // ─── Giao diện ───────────────────────────────────────────

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UIConstants.MAU_NEN);
        setBorder(new EmptyBorder(20, 25, 20, 25));
        add(taoPhanTieuDe(), BorderLayout.NORTH);
        add(taoBang(),       BorderLayout.CENTER);
        add(taoThongBao(),   BorderLayout.SOUTH);
    }

    private JPanel taoPhanTieuDe() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel("Quản lý Chuyên khoa");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(UIConstants.MAU_NHAN);

        JButton btnThem = new JButton("+ Thêm khoa");
        btnThem.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnThem.setBackground(UIConstants.MAU_THANH_CONG);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        btnThem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(e -> xuLyThemKhoa());

        p.add(lbl,     BorderLayout.WEST);
        p.add(btnThem, BorderLayout.EAST);
        return p;
    }

    private JScrollPane taoBang() {
        tableModel = new DefaultTableModel(COT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(UIConstants.FONT_ADMIN);
        table.getTableHeader().setFont(UIConstants.FONT_ADMIN_BOLD);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // Mã khoa
        table.getColumnModel().getColumn(1).setPreferredWidth(350);  // Tên khoa
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // Trạng thái

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && (SwingUtilities.isRightMouseButton(e) || e.getClickCount() == 2)) {
                    table.setRowSelectionInterval(row, row);
                    hienMenu(row, e);
                }
            }
        });
        return new JScrollPane(table);
    }

    private JLabel taoThongBao() {
        lblThongBao = new JLabel(" ");
        lblThongBao.setFont(UIConstants.FONT_ADMIN_BOLD);
        return lblThongBao;
    }

    // ─── Thao tác ────────────────────────────────────────────

    private void hienMenu(int row, java.awt.event.MouseEvent e) {
        int    maKhoa    = (int)    tableModel.getValueAt(row, 0);
        String tenKhoa   = (String) tableModel.getValueAt(row, 1);
        String trangThai = (String) tableModel.getValueAt(row, 2);
        boolean dangOn   = trangThai.contains("Hoạt động");

        JPopupMenu popup = new JPopupMenu();

        JMenuItem miToggle = new JMenuItem(dangOn ? "🔴  Tắt khoa" : "🟢  Bật khoa");
        miToggle.setFont(UIConstants.FONT_ADMIN);
        miToggle.addActionListener(ev -> xuLyToggle(maKhoa, !dangOn));

        JMenuItem miXoa = new JMenuItem("🗑  Xóa khoa");
        miXoa.setFont(UIConstants.FONT_ADMIN);
        miXoa.addActionListener(ev -> xuLyXoa(maKhoa, tenKhoa));

        popup.add(miToggle);
        popup.addSeparator();
        popup.add(miXoa);
        popup.show(table, e.getX(), e.getY());
    }

    private void xuLyThemKhoa() {
        String ten = JOptionPane.showInputDialog(this,
                "Nhập tên chuyên khoa mới:", "Thêm khoa", JOptionPane.PLAIN_MESSAGE);
        if (ten == null || ten.trim().isEmpty()) return;

        NhanVienQuanLy nv = adminApp.getNhanVienHienTai();
        ChuyenKhoa k = new ChuyenKhoa(0, ten.trim(), true, nv != null ? nv.getMaNhanVien() : 1);
        try {
            controller.themKhoa(k);
            hienThongBao("✅ Đã thêm khoa: " + ten, UIConstants.MAU_THANH_CONG);
            taiDuLieu();
        } catch (IllegalArgumentException ex) {
            hienThongBao("❌ " + ex.getMessage(), UIConstants.MAU_LOI);
        }
    }

    private void xuLyXoa(int maKhoa, String tenKhoa) {
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa khoa \"" + tenKhoa + "\"?\nSẽ xóa toàn bộ bác sĩ, hàng chờ, phiếu khám liên quan!",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        if (controller.xoaKhoa(maKhoa)) {
            hienThongBao("✅ Đã xóa khoa: " + tenKhoa, UIConstants.MAU_THANH_CONG);
            taiDuLieu();
        } else {
            hienThongBao("❌ Xóa thất bại. Xem log để biết chi tiết.", UIConstants.MAU_LOI);
        }
    }

    private void xuLyToggle(int maKhoa, boolean trangThaiMoi) {
        controller.capNhatTrangThai(maKhoa, trangThaiMoi);
        hienThongBao(trangThaiMoi ? "✅ Đã bật khoa." : "⚠ Đã tắt khoa.",
                trangThaiMoi ? UIConstants.MAU_THANH_CONG : UIConstants.MAU_LOI);
        taiDuLieu();
    }

    private void hienThongBao(String msg, Color mau) {
        lblThongBao.setText(msg);
        lblThongBao.setForeground(mau);
        Timer t = new Timer(4000, e -> lblThongBao.setText(" "));
        t.setRepeats(false); t.start();
    }
}
