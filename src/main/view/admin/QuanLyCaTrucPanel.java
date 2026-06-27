package view.admin;

import controller.BacSiController;
import controller.CaTrucController;
import model.BacSi;
import model.CaTruc;
import model.NhanVienQuanLy;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel quản lý ca trực (UC15, UC16, UC17).
 * FIX: SwingWorker tải dữ liệu tránh lag; xóa ca cascade qua DAO.
 */
public class QuanLyCaTrucPanel extends JPanel {

    private final AdminApp         adminApp;
    private final CaTrucController caTrucCtrl = new CaTrucController();
    private final BacSiController  bacSiCtrl  = new BacSiController();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            lblThongBao;

    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_GIO  = DateTimeFormatter.ofPattern("HH:mm");

    private static final String[] COT = {
        "Mã ca", "Tên ca", "Ngày trực", "Giờ BD", "Giờ KT", "Bác sĩ phụ trách"
    };

    public QuanLyCaTrucPanel(AdminApp adminApp) {
        this.adminApp = adminApp;
        khoiTaoGiaoDien();
    }

    /** Tải danh sách ca trực trên background thread. */
    public void taiDuLieu() {
        new SwingWorker<List<CaTruc>, Void>() {
            @Override protected List<CaTruc> doInBackground() {
                return caTrucCtrl.layDanhSachCaTruc();
            }
            @Override protected void done() {
                try { capNhatBang(get()); }
                catch (Exception e) { hienThongBao("❌ Lỗi tải dữ liệu", UIConstants.MAU_LOI); }
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

        JLabel lbl = new JLabel("Quản lý Ca trực");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(UIConstants.MAU_NHAN);

        JButton btnThem = new JButton("+ Thêm ca trực");
        btnThem.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnThem.setBackground(UIConstants.MAU_THANH_CONG);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        btnThem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(e -> hienDialogThemCaTruc());

        p.add(lbl,     BorderLayout.WEST);
        p.add(btnThem, BorderLayout.EAST);
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
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(300);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && SwingUtilities.isRightMouseButton(e)) {
                    table.setRowSelectionInterval(row, row);
                    hienMenuXoa(row, e);
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

    // ─── Cập nhật bảng ───────────────────────────────────────

    private void capNhatBang(List<CaTruc> ds) {
        tableModel.setRowCount(0);
        for (CaTruc ct : ds) {
            String tenBacSi = ct.getDanhSachBacSi().stream()
                    .map(BacSi::getTenBacSi)
                    .collect(Collectors.joining(", "));
            tableModel.addRow(new Object[]{
                ct.getMaCaTruc(),
                ct.getTenCaTruc(),
                ct.getNgayTruc()   != null ? ct.getNgayTruc().format(FMT_NGAY)   : "",
                ct.getGioBatDau()  != null ? ct.getGioBatDau().format(FMT_GIO)   : "",
                ct.getGioKetThuc() != null ? ct.getGioKetThuc().format(FMT_GIO)  : "",
                tenBacSi.isEmpty() ? "(Chưa có bác sĩ)" : tenBacSi
            });
        }
    }

    // ─── Dialog thêm ca trực ─────────────────────────────────

    private void hienDialogThemCaTruc() {
        // Tải bác sĩ trước khi mở dialog
        List<BacSi> tatCaBacSi = bacSiCtrl.layTatCaBacSi()
                .stream().filter(BacSi::isTrangThai).collect(Collectors.toList());

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Thêm ca trực", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        // ── Form nhập liệu ────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JTextField txtTenCa = new JTextField();
        JTextField txtNgay  = new JTextField(LocalDate.now().format(FMT_NGAY));
        JTextField txtGioBD = new JTextField("07:00");
        JTextField txtGioKT = new JTextField("11:30");

        DefaultListModel<String> listModel = new DefaultListModel<>();
        tatCaBacSi.forEach(b -> listModel.addElement(b.getTenBacSi()));
        JList<String> listBacSi = new JList<>(listModel);
        listBacSi.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listBacSi.setFont(UIConstants.FONT_ADMIN);
        listBacSi.setVisibleRowCount(4);

        form.add(new JLabel("Tên ca trực:"));          form.add(txtTenCa);
        form.add(new JLabel("Ngày trực (dd/MM/yyyy):")); form.add(txtNgay);
        form.add(new JLabel("Giờ bắt đầu (HH:mm):"));  form.add(txtGioBD);
        form.add(new JLabel("Giờ kết thúc (HH:mm):"));  form.add(txtGioKT);
        form.add(new JLabel("Bác sĩ phụ trách:"));
        form.add(new JScrollPane(listBacSi));

        // ── Label lỗi ─────────────────────────────────────────
        JLabel lblLoi = new JLabel(" ", SwingConstants.CENTER);
        lblLoi.setForeground(UIConstants.MAU_LOI);
        lblLoi.setFont(UIConstants.FONT_ADMIN_BOLD);
        lblLoi.setBorder(new EmptyBorder(0, 20, 5, 20));

        // ── Nút Lưu / Hủy ────────────────────────────────────
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnHuy = new JButton("Hủy");
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(UIConstants.MAU_NHAN);
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnHuy.addActionListener(e -> dlg.dispose());
        btnLuu.addActionListener(e -> {
            String loi = xuLyLuuCaTruc(
                    txtTenCa, txtNgay, txtGioBD, txtGioKT, listBacSi, tatCaBacSi);
            if (loi == null) { dlg.dispose(); taiDuLieu(); }
            else lblLoi.setText(loi);
        });
        btnP.add(btnHuy); btnP.add(btnLuu);

        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(lblLoi, BorderLayout.NORTH);
        dlg.add(btnP,   BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    /**
     * Validate + gọi controller thêm ca trực.
     * @return null nếu thành công, chuỗi lỗi nếu thất bại.
     */
    private String xuLyLuuCaTruc(JTextField txtTen, JTextField txtNgay,
                                   JTextField txtGioBD, JTextField txtGioKT,
                                   JList<String> listBacSi, List<BacSi> tatCaBacSi) {
        if (txtTen.getText().trim().isEmpty())
            return "Tên ca trực không được để trống.";
        if (listBacSi.getSelectedIndices().length == 0)
            return "Vui lòng chọn ít nhất 1 bác sĩ.";

        LocalDate ngay; LocalTime gioBD, gioKT;
        try {
            ngay  = LocalDate.parse(txtNgay.getText().trim(), FMT_NGAY);
            gioBD = LocalTime.parse(txtGioBD.getText().trim(), FMT_GIO);
            gioKT = LocalTime.parse(txtGioKT.getText().trim(), FMT_GIO);
        } catch (DateTimeParseException ex) {
            return "Định dạng ngày (dd/MM/yyyy) hoặc giờ (HH:mm) không đúng.";
        }
        if (!gioBD.isBefore(gioKT))
            return "Giờ bắt đầu phải trước giờ kết thúc.";

        List<BacSi> chon = new ArrayList<>();
        for (int i : listBacSi.getSelectedIndices()) chon.add(tatCaBacSi.get(i));

        NhanVienQuanLy nv = adminApp.getNhanVienHienTai();
        CaTruc ct = new CaTruc(0, txtTen.getText().trim(), gioBD, gioKT, ngay,
                nv != null ? nv.getMaNhanVien() : 1);

        List<String> xungDot = caTrucCtrl.themCaTrucVoiKiemTraXungDot(ct, chon);
        if (!xungDot.isEmpty())
            return "Bị xung đột lịch: " + String.join(", ", xungDot);

        hienThongBao("✅ Đã thêm ca trực: " + ct.getTenCaTruc(), UIConstants.MAU_THANH_CONG);
        return null;
    }

    // ─── Menu xóa ────────────────────────────────────────────

    private void hienMenuXoa(int row, java.awt.event.MouseEvent e) {
        int    maCaTruc = (int)    tableModel.getValueAt(row, 0);
        String tenCa    = (String) tableModel.getValueAt(row, 1);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem  miXoa = new JMenuItem("🗑  Xóa ca trực");
        miXoa.setFont(UIConstants.FONT_ADMIN);
        miXoa.addActionListener(ev -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Xóa ca trực \"" + tenCa + "\"?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                if (caTrucCtrl.xoaCaTruc(maCaTruc)) {
                    hienThongBao("✅ Đã xóa ca trực: " + tenCa, UIConstants.MAU_THANH_CONG);
                    taiDuLieu();
                } else {
                    hienThongBao("❌ Xóa thất bại.", UIConstants.MAU_LOI);
                }
            }
        });
        popup.add(miXoa);
        popup.show(table, e.getX(), e.getY());
    }

    private void hienThongBao(String msg, Color mau) {
        lblThongBao.setText(msg); lblThongBao.setForeground(mau);
        Timer t = new Timer(4000, e -> lblThongBao.setText(" "));
        t.setRepeats(false); t.start();
    }
}
