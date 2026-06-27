package view.admin;

import controller.BacSiController;
import controller.ChuyenKhoaController;
import model.BacSi;
import model.ChuyenKhoa;
import model.NhanVienQuanLy;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý bác sĩ (UC11–UC14).
 * FIX: hiển thị cột Mã bác sĩ; SwingWorker tải dữ liệu tránh lag;
 *      xóa bác sĩ cascade (không còn lỗi FK).
 */
public class QuanLyBacSiPanel extends JPanel {

    private final AdminApp             adminApp;
    private final BacSiController      bacSiCtrl  = new BacSiController();
    private final ChuyenKhoaController khoaCtrl   = new ChuyenKhoaController();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbLocKhoa;
    private List<ChuyenKhoa>  danhSachKhoa;
    private JLabel            lblThongBao;

    // FIX: maBacSi hiện ở cột đầu thay vì bị ẩn
    private static final String[] COT = {
        "Mã BS", "Tên bác sĩ", "Khoa", "Trạng thái", "Chờ hôm nay"
    };

    public QuanLyBacSiPanel(AdminApp adminApp) {
        this.adminApp = adminApp;
        khoiTaoGiaoDien();
    }

    public void taiDuLieu() {
        taiDanhSachKhoa();
        taiDanhSachBacSi();
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
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel("Quản lý Bác sĩ");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(UIConstants.MAU_NHAN);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        cmbLocKhoa = new JComboBox<>();
        cmbLocKhoa.setFont(UIConstants.FONT_ADMIN);
        cmbLocKhoa.setPreferredSize(new Dimension(200, 32));
        cmbLocKhoa.addActionListener(e -> taiDanhSachBacSi());

        JButton btnThem = new JButton("+ Thêm bác sĩ");
        btnThem.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnThem.setBackground(UIConstants.MAU_THANH_CONG);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        btnThem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(e -> hienDialogThemSua(null));

        right.add(new JLabel("Lọc khoa:"));
        right.add(cmbLocKhoa);
        right.add(btnThem);

        p.add(lbl,   BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JScrollPane taoBang() {
        tableModel = new DefaultTableModel(COT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setFont(UIConstants.FONT_ADMIN);
        table.getTableHeader().setFont(UIConstants.FONT_ADMIN_BOLD);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // Mã BS
        table.getColumnModel().getColumn(1).setPreferredWidth(220);  // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(160);  // Khoa
        table.getColumnModel().getColumn(3).setPreferredWidth(110);  // Trạng thái
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Số chờ

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

    // ─── Tải dữ liệu (SwingWorker) ───────────────────────────

    private void taiDanhSachKhoa() {
        danhSachKhoa = khoaCtrl.layTatCaKhoa();
        cmbLocKhoa.removeAllItems();
        cmbLocKhoa.addItem("-- Tất cả khoa --");
        danhSachKhoa.forEach(k -> cmbLocKhoa.addItem(k.getTenKhoa()));
    }

    private void taiDanhSachBacSi() {
        int idxKhoa = cmbLocKhoa.getSelectedIndex();
        new SwingWorker<List<BacSi>, Void>() {
            @Override protected List<BacSi> doInBackground() {
                return bacSiCtrl.layTatCaBacSi();
            }
            @Override protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<BacSi> ds = get();
                    for (BacSi bs : ds) {
                        if (idxKhoa > 0 && danhSachKhoa != null) {
                            ChuyenKhoa chon = danhSachKhoa.get(idxKhoa - 1);
                            if (bs.getMaKhoa() != chon.getMaKhoa()) continue;
                        }
                        tableModel.addRow(new Object[]{
                            bs.getMaBacSi(),
                            bs.getTenBacSi(),
                            bs.getTenKhoa(),
                            bs.isTrangThai() ? "✅ Hoạt động" : "🔴 Tắt",
                            bs.getSoNguoiCho()
                        });
                    }
                } catch (Exception e) {
                    hienThongBao("❌ Lỗi tải dữ liệu", UIConstants.MAU_LOI);
                }
            }
        }.execute();
    }

    // ─── Thao tác ────────────────────────────────────────────

    private void hienMenu(int row, java.awt.event.MouseEvent e) {
        int    maBacSi   = (int)    tableModel.getValueAt(row, 0);
        String tenBacSi  = (String) tableModel.getValueAt(row, 1);
        String trangThai = (String) tableModel.getValueAt(row, 3);
        boolean dangOn   = trangThai.contains("Hoạt động");

        JPopupMenu popup = new JPopupMenu();

        JMenuItem miSua = new JMenuItem("✏  Sửa thông tin");
        miSua.setFont(UIConstants.FONT_ADMIN);
        miSua.addActionListener(ev -> {
            BacSi bs = timBacSi(maBacSi);
            if (bs != null) hienDialogThemSua(bs);
        });

        JMenuItem miToggle = new JMenuItem(dangOn ? "🔴  Tắt" : "🟢  Bật");
        miToggle.setFont(UIConstants.FONT_ADMIN);
        miToggle.addActionListener(ev -> xuLyToggle(maBacSi, !dangOn));

        JMenuItem miXoa = new JMenuItem("🗑  Xóa");
        miXoa.setFont(UIConstants.FONT_ADMIN);
        miXoa.addActionListener(ev -> xuLyXoa(maBacSi, tenBacSi));

        popup.add(miSua);
        popup.add(miToggle);
        popup.addSeparator();
        popup.add(miXoa);
        popup.show(table, e.getX(), e.getY());
    }

    private void hienDialogThemSua(BacSi bacSiCu) {
        boolean laThemMoi = (bacSiCu == null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                laThemMoi ? "Thêm bác sĩ" : "Sửa bác sĩ",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(380, 220);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JTextField txtTen = new JTextField(laThemMoi ? "" : bacSiCu.getTenBacSi());
        JComboBox<String> cmbKhoa = new JComboBox<>();
        if (danhSachKhoa != null) danhSachKhoa.forEach(k -> cmbKhoa.addItem(k.getTenKhoa()));
        if (!laThemMoi && danhSachKhoa != null) {
            for (int i = 0; i < danhSachKhoa.size(); i++)
                if (danhSachKhoa.get(i).getMaKhoa() == bacSiCu.getMaKhoa()) {
                    cmbKhoa.setSelectedIndex(i); break;
                }
        }

        form.add(new JLabel("Tên bác sĩ:")); form.add(txtTen);
        form.add(new JLabel("Chuyên khoa:")); form.add(cmbKhoa);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");
        btnLuu.setBackground(UIConstants.MAU_NHAN);
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnHuy.addActionListener(e -> dlg.dispose());
        btnLuu.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) { JOptionPane.showMessageDialog(dlg, "Tên không được rỗng!"); return; }
            int idx = cmbKhoa.getSelectedIndex();
            if (idx < 0 || danhSachKhoa == null) return;
            ChuyenKhoa k = danhSachKhoa.get(idx);
            NhanVienQuanLy nv = adminApp.getNhanVienHienTai();
            int maNV = nv != null ? nv.getMaNhanVien() : 1;

            if (laThemMoi) {
                bacSiCtrl.themBacSi(new BacSi(0, ten, k.getMaKhoa(), true, maNV));
                hienThongBao("✅ Đã thêm bác sĩ: " + ten, UIConstants.MAU_THANH_CONG);
            } else {
                bacSiCu.setTenBacSi(ten); bacSiCu.setMaKhoa(k.getMaKhoa());
                bacSiCtrl.capNhatBacSi(bacSiCu);
                hienThongBao("✅ Đã cập nhật: " + ten, UIConstants.MAU_THANH_CONG);
            }
            dlg.dispose(); taiDanhSachBacSi();
        });

        btnP.add(btnHuy); btnP.add(btnLuu);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void xuLyToggle(int maBacSi, boolean trangThaiMoi) {
        bacSiCtrl.capNhatTrangThai(maBacSi, trangThaiMoi);
        hienThongBao(trangThaiMoi ? "✅ Đã bật bác sĩ." : "⚠ Đã tắt bác sĩ.",
                trangThaiMoi ? UIConstants.MAU_THANH_CONG : UIConstants.MAU_LOI);
        taiDanhSachBacSi();
    }

    private void xuLyXoa(int maBacSi, String tenBacSi) {
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa bác sĩ \"" + tenBacSi + "\"?\nSẽ xóa toàn bộ hàng chờ và phiếu khám liên quan!",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        if (bacSiCtrl.xoaBacSi(maBacSi)) {
            hienThongBao("✅ Đã xóa bác sĩ: " + tenBacSi, UIConstants.MAU_THANH_CONG);
            taiDanhSachBacSi();
        } else {
            hienThongBao("❌ Xóa thất bại. Xem log để biết chi tiết.", UIConstants.MAU_LOI);
        }
    }

    private BacSi timBacSi(int maBacSi) {
        return bacSiCtrl.layTatCaBacSi().stream()
                .filter(b -> b.getMaBacSi() == maBacSi).findFirst().orElse(null);
    }

    private void hienThongBao(String msg, Color mau) {
        lblThongBao.setText(msg); lblThongBao.setForeground(mau);
        Timer t = new Timer(4000, e -> lblThongBao.setText(" "));
        t.setRepeats(false); t.start();
    }
}
