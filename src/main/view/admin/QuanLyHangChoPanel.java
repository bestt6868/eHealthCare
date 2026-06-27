package view.admin;

import controller.HangChoController;
import model.HangCho;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel quản lý hàng chờ hôm nay (UC18, UC19, UC20).
 * FIX: SwingWorker tải dữ liệu tránh lag; reset xóa hẳn hàng HANG_CHO + PHIEU_KHAM.
 */
public class QuanLyHangChoPanel extends JPanel {

    private final AdminApp          adminApp;
    private final HangChoController controller = new HangChoController();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            lblTieuDe;
    private JLabel            lblThongBao;
    private JLabel            lblTongCho;

    private static final String[] COT = {"STT", "Bác sĩ", "Khoa", "Số BN đang chờ"};

    public QuanLyHangChoPanel(AdminApp adminApp) {
        this.adminApp = adminApp;
        khoiTaoGiaoDien();
    }

    /** Tải dữ liệu trên background thread tránh lag. */
    public void taiDuLieu() {
        new SwingWorker<List<HangCho>, Void>() {
            @Override protected List<HangCho> doInBackground() {
                return controller.xemHangChoHomNay();
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
        add(taoPhanDuoi(),   BorderLayout.SOUTH);
    }

    private JPanel taoPhanTieuDe() {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        lblTieuDe = new JLabel("Hàng chờ hôm nay");
        lblTieuDe.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTieuDe.setForeground(UIConstants.MAU_NHAN);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnLamMoi = new JButton("🔄 Làm mới");
        btnLamMoi.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnLamMoi.setBackground(UIConstants.MAU_NHAN);
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLamMoi.addActionListener(e -> {
            taiDuLieu();
            hienThongBao("✅ Đã cập nhật dữ liệu mới nhất.", UIConstants.MAU_THANH_CONG);
        });

        JButton btnReset = new JButton("⚠ Reset hàng chờ");
        btnReset.setFont(UIConstants.FONT_ADMIN_BOLD);
        btnReset.setBackground(UIConstants.MAU_LOI);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> xuLyReset());

        right.add(btnLamMoi);
        right.add(btnReset);
        p.add(lblTieuDe, BorderLayout.WEST);
        p.add(right,     BorderLayout.EAST);
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

        javax.swing.table.DefaultTableCellRenderer center =
                new javax.swing.table.DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        return new JScrollPane(table);
    }

    private JPanel taoPhanDuoi() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 0, 0, 0));

        lblTongCho = new JLabel("Tổng bệnh nhân đang chờ: 0");
        lblTongCho.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTongCho.setForeground(UIConstants.MAU_NHAN);

        lblThongBao = new JLabel(" ");
        lblThongBao.setFont(UIConstants.FONT_ADMIN_BOLD);

        p.add(lblTongCho,  BorderLayout.WEST);
        p.add(lblThongBao, BorderLayout.CENTER);
        return p;
    }

    // ─── Cập nhật bảng ───────────────────────────────────────

    private void capNhatBang(List<HangCho> ds) {
        tableModel.setRowCount(0);
        int stt = 1, tong = 0;
        for (HangCho hc : ds) {
            tableModel.addRow(new Object[]{
                stt++,
                hc.getTenBacSi()  != null ? hc.getTenBacSi()  : "---",
                hc.getTenKhoa()   != null ? hc.getTenKhoa()   : "---",
                hc.getSoNguoiCho()
            });
            tong += hc.getSoNguoiCho();
        }
        String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        lblTieuDe.setText("Hàng chờ hôm nay – " + ngay);
        lblTongCho.setText("Tổng bệnh nhân đang chờ: " + tong);
    }

    // ─── Xử lý reset ─────────────────────────────────────────

    private void xuLyReset() {
        int ok = JOptionPane.showConfirmDialog(this,
                "Reset toàn bộ hàng chờ hôm nay?\n"
              + "Tất cả PHIẾU KHÁM và HÀNG CHỜ trong ngày sẽ bị XÓA VĨNH VIỄN!",
                "Xác nhận Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return controller.resetHangCho(); }
            @Override protected void done() {
                try {
                    if (get()) {
                        taiDuLieu();
                        hienThongBao("✅ Đã reset toàn bộ hàng chờ hôm nay.", UIConstants.MAU_THANH_CONG);
                    } else {
                        hienThongBao("❌ Reset thất bại.", UIConstants.MAU_LOI);
                    }
                } catch (Exception e) {
                    hienThongBao("❌ Lỗi: " + e.getMessage(), UIConstants.MAU_LOI);
                }
            }
        }.execute();
    }

    private void hienThongBao(String msg, Color mau) {
        lblThongBao.setText(msg); lblThongBao.setForeground(mau);
        Timer t = new Timer(4000, e -> lblThongBao.setText(" "));
        t.setRepeats(false); t.start();
    }
}
