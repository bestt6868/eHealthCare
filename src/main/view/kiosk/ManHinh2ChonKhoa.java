package view.kiosk;

import controller.KioskController;
import model.ChuyenKhoa;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Màn hình 2 – Chọn chuyên khoa (UC02).
 * FIX: dùng SwingWorker tải khoa tránh lag UI.
 */
public class ManHinh2ChonKhoa extends JPanel {

    private final KioskApp        kioskApp;
    private final KioskController controller = new KioskController();

    private JLabel lblChaoHoi;
    private JPanel panelDanhSachKhoa;
    private JLabel lblTrangThai;

    public ManHinh2ChonKhoa(KioskApp kioskApp) {
        this.kioskApp = kioskApp;
        khoiTaoGiaoDien();
    }

    public void khoiTaoLai() {
        if (kioskApp.getBenhNhanHienTai() != null)
            lblChaoHoi.setText("Xin chào, " + kioskApp.getBenhNhanHienTai().getHoTen());
        taiDanhSachKhoa();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 15));
        setBackground(UIConstants.MAU_NEN);
        setBorder(new EmptyBorder(30, 50, 30, 50));
        add(taoPhanTren(), BorderLayout.NORTH);
        add(taoPhanGiua(), BorderLayout.CENTER);
        add(taoPhanDuoi(), BorderLayout.SOUTH);
    }

    private JPanel taoPhanTren() {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);

        lblChaoHoi = new JLabel("Xin chào, ...");
        lblChaoHoi.setFont(UIConstants.FONT_KIOSK);
        lblChaoHoi.setForeground(UIConstants.MAU_CHU);

        JLabel lblTieuDe = new JLabel("CHỌN KHOA KHÁM", SwingConstants.CENTER);
        lblTieuDe.setFont(UIConstants.FONT_TIEU_DE);
        lblTieuDe.setForeground(UIConstants.MAU_NHAN);

        lblTrangThai = new JLabel("", SwingConstants.CENTER);
        lblTrangThai.setFont(UIConstants.FONT_ADMIN);
        lblTrangThai.setForeground(UIConstants.MAU_CHU);

        p.add(lblChaoHoi, BorderLayout.NORTH);
        p.add(lblTieuDe,  BorderLayout.CENTER);
        p.add(lblTrangThai, BorderLayout.SOUTH);
        return p;
    }

    private JScrollPane taoPhanGiua() {
        panelDanhSachKhoa = new JPanel(new GridLayout(0, 2,
                UIConstants.KHOANG_CACH_NUT * 2, UIConstants.KHOANG_CACH_NUT * 2));
        panelDanhSachKhoa.setBackground(UIConstants.MAU_NEN);
        panelDanhSachKhoa.setBorder(new EmptyBorder(10, 0, 10, 0));
        JScrollPane sp = new JScrollPane(panelDanhSachKhoa);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel taoPhanDuoi() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JButton btn = new JButton("← QUAY LẠI");
        btn.setFont(UIConstants.FONT_NUT);
        btn.setPreferredSize(new Dimension(200, UIConstants.NUT_CAO_MIN));
        btn.setBackground(UIConstants.MAU_BORDER);
        btn.setForeground(UIConstants.MAU_CHU);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> kioskApp.resetPhien());
        p.add(btn);
        return p;
    }

    /** Tải danh sách khoa trên background thread để không lag. */
    private void taiDanhSachKhoa() {
        panelDanhSachKhoa.removeAll();
        lblTrangThai.setText("Đang tải danh sách khoa...");

        new SwingWorker<List<ChuyenKhoa>, Void>() {
            @Override protected List<ChuyenKhoa> doInBackground() {
                return controller.layDanhSachKhoa();
            }
            @Override protected void done() {
                try {
                    List<ChuyenKhoa> ds = get();
                    lblTrangThai.setText("");
                    if (ds.isEmpty()) {
                        JLabel l = new JLabel("Hôm nay chưa có khoa nào có bác sĩ trực.",
                                SwingConstants.CENTER);
                        l.setFont(UIConstants.FONT_KIOSK);
                        panelDanhSachKhoa.add(l);
                    } else {
                        for (ChuyenKhoa k : ds)
                            panelDanhSachKhoa.add(taoNutKhoa(k));
                    }
                    panelDanhSachKhoa.revalidate();
                    panelDanhSachKhoa.repaint();
                } catch (Exception e) {
                    lblTrangThai.setText("Lỗi tải dữ liệu.");
                }
            }
        }.execute();
    }

    private JButton taoNutKhoa(ChuyenKhoa khoa) {
        JButton nut = new JButton(khoa.getTenKhoa());
        nut.setFont(new Font("SansSerif", Font.BOLD, 18));
        nut.setPreferredSize(new Dimension(UIConstants.NUT_KHOA_RONG, UIConstants.NUT_KHOA_CAO));
        nut.setBackground(UIConstants.MAU_NHAN);
        nut.setForeground(Color.WHITE);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.addActionListener(e -> {
            kioskApp.setChuyenKhoaHienTai(khoa);
            kioskApp.chuyenManHinh(KioskApp.MAN_HINH_CHON_BAC_SI);
        });
        return nut;
    }
}
