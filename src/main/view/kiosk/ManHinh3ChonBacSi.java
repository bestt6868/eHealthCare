package view.kiosk;

import controller.KioskController;
import model.BacSi;
import model.ChuyenKhoa;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Màn hình 3 – Chọn bác sĩ (UC03, UC04).
 * FIX: SwingWorker tải bác sĩ tránh lag; chỉ hiện bác sĩ có ca trực hôm nay.
 */
public class ManHinh3ChonBacSi extends JPanel {

    private final KioskApp        kioskApp;
    private final KioskController controller = new KioskController();

    private JLabel lblTieuDe;
    private JPanel panelDanhSachBacSi;
    private JLabel lblTrangThai;
    private JLabel lblThongBaoLoi;

    public ManHinh3ChonBacSi(KioskApp kioskApp) {
        this.kioskApp = kioskApp;
        khoiTaoGiaoDien();
    }

    public void khoiTaoLai() {
        ChuyenKhoa ck = kioskApp.getChuyenKhoaHienTai();
        lblTieuDe.setText("CHỌN BÁC SĨ" + (ck != null ? " – Khoa " + ck.getTenKhoa() : ""));
        lblThongBaoLoi.setVisible(false);
        taiDanhSachBacSi();
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
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        lblTieuDe = new JLabel("CHỌN BÁC SĨ", SwingConstants.CENTER);
        lblTieuDe.setFont(UIConstants.FONT_TIEU_DE);
        lblTieuDe.setForeground(UIConstants.MAU_NHAN);

        lblTrangThai = new JLabel("", SwingConstants.CENTER);
        lblTrangThai.setFont(UIConstants.FONT_ADMIN);

        lblThongBaoLoi = new JLabel("", SwingConstants.CENTER);
        lblThongBaoLoi.setFont(UIConstants.FONT_ADMIN_BOLD);
        lblThongBaoLoi.setForeground(UIConstants.MAU_LOI);
        lblThongBaoLoi.setVisible(false);

        p.add(lblTieuDe,       BorderLayout.CENTER);
        p.add(lblTrangThai,    BorderLayout.NORTH);
        p.add(lblThongBaoLoi,  BorderLayout.SOUTH);
        return p;
    }

    private JScrollPane taoPhanGiua() {
        panelDanhSachBacSi = new JPanel();
        panelDanhSachBacSi.setLayout(new BoxLayout(panelDanhSachBacSi, BoxLayout.Y_AXIS));
        panelDanhSachBacSi.setBackground(UIConstants.MAU_NEN);
        JScrollPane sp = new JScrollPane(panelDanhSachBacSi);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel taoPhanDuoi() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        p.setOpaque(false);

        JButton btnQuayLai = new JButton("← QUAY LẠI");
        btnQuayLai.setFont(UIConstants.FONT_NUT);
        btnQuayLai.setPreferredSize(new Dimension(200, UIConstants.NUT_CAO_MIN));
        btnQuayLai.setBackground(UIConstants.MAU_BORDER);
        btnQuayLai.setForeground(UIConstants.MAU_CHU);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> kioskApp.chuyenManHinh(KioskApp.MAN_HINH_CHON_KHOA));

        JButton btnBoQua = new JButton("BỎ QUA – Tự chọn bác sĩ");
        btnBoQua.setFont(UIConstants.FONT_NUT);
        btnBoQua.setPreferredSize(new Dimension(280, UIConstants.NUT_CAO_MIN));
        btnBoQua.setBackground(new Color(0xFF8F00));
        btnBoQua.setForeground(Color.WHITE);
        btnBoQua.setFocusPainted(false);
        btnBoQua.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBoQua.addActionListener(e -> xuLyBoQua());

        p.add(btnQuayLai);
        p.add(btnBoQua);
        return p;
    }

    /** Tải bác sĩ trên background thread tránh lag. */
    private void taiDanhSachBacSi() {
        panelDanhSachBacSi.removeAll();
        lblTrangThai.setText("Đang tải danh sách bác sĩ...");

        ChuyenKhoa ck = kioskApp.getChuyenKhoaHienTai();
        if (ck == null) return;

        new SwingWorker<List<BacSi>, Void>() {
            @Override protected List<BacSi> doInBackground() {
                return controller.layDanhSachBacSiTheoKhoa(ck.getMaKhoa());
            }
            @Override protected void done() {
                try {
                    List<BacSi> ds = get();
                    lblTrangThai.setText("");
                    if (ds.isEmpty()) {
                        JLabel l = new JLabel("Không có bác sĩ nào có ca trực hôm nay.",
                                SwingConstants.CENTER);
                        l.setFont(UIConstants.FONT_KIOSK);
                        panelDanhSachBacSi.add(l);
                    } else {
                        for (BacSi bs : ds) {
                            panelDanhSachBacSi.add(taoHangBacSi(bs));
                            panelDanhSachBacSi.add(Box.createVerticalStrut(8));
                        }
                    }
                    panelDanhSachBacSi.revalidate();
                    panelDanhSachBacSi.repaint();
                } catch (Exception e) {
                    lblTrangThai.setText("Lỗi tải dữ liệu.");
                }
            }
        }.execute();
    }

    private JPanel taoHangBacSi(BacSi bs) {
        JPanel hang = new JPanel(new BorderLayout(15, 0));
        hang.setBackground(Color.WHITE);
        hang.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.MAU_BORDER, 1),
                new EmptyBorder(12, 20, 12, 20)));
        hang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);

        JLabel lblTen = new JLabel(bs.getTenBacSi());
        lblTen.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel lblCho = new JLabel("Đang chờ: " + bs.getSoNguoiCho() + " bệnh nhân");
        lblCho.setFont(UIConstants.FONT_ADMIN);
        lblCho.setForeground(bs.getSoNguoiCho() == 0
                ? UIConstants.MAU_THANH_CONG : UIConstants.MAU_CHU);

        info.add(lblTen);
        info.add(lblCho);

        JButton btnChon = new JButton("CHỌN");
        btnChon.setFont(UIConstants.FONT_NUT);
        btnChon.setPreferredSize(new Dimension(120, UIConstants.NUT_CAO_MIN));
        btnChon.setBackground(UIConstants.MAU_NHAN);
        btnChon.setForeground(Color.WHITE);
        btnChon.setFocusPainted(false);
        btnChon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChon.addActionListener(e -> {
            kioskApp.setBacSiHienTai(bs);
            kioskApp.chuyenManHinh(KioskApp.MAN_HINH_XAC_NHAN);
        });

        hang.add(info,    BorderLayout.CENTER);
        hang.add(btnChon, BorderLayout.EAST);
        return hang;
    }

    private void xuLyBoQua() {
        ChuyenKhoa ck = kioskApp.getChuyenKhoaHienTai();
        if (ck == null) return;
        try {
            BacSi bs = controller.chonBacSiTuDong(ck.getMaKhoa());
            kioskApp.setBacSiHienTai(bs);
            kioskApp.chuyenManHinh(KioskApp.MAN_HINH_XAC_NHAN);
        } catch (RuntimeException ex) {
            lblThongBaoLoi.setText(ex.getMessage());
            lblThongBaoLoi.setVisible(true);
        }
    }
}
