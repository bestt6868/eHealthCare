package model;

/**
 * Entity đại diện cho bảng trung gian N-N giữa CA_TRUC và BAC_SI.
 * Ánh xạ với bảng LICH_CA_TRUC trong cơ sở dữ liệu.
 */
public class LichCaTruc {

    private int maCaTruc;
    private int maBacSi;

    /** Constructor mặc định */
    public LichCaTruc() {}

    /** Constructor đầy đủ tham số */
    public LichCaTruc(int maCaTruc, int maBacSi) {
        this.maCaTruc = maCaTruc;
        this.maBacSi  = maBacSi;
    }

    public int  getMaCaTruc()               { return maCaTruc; }
    public void setMaCaTruc(int maCaTruc)   { this.maCaTruc = maCaTruc; }

    public int  getMaBacSi()             { return maBacSi; }
    public void setMaBacSi(int maBacSi)  { this.maBacSi = maBacSi; }

    @Override
    public String toString() {
        return "LichCaTruc{maCaTruc=" + maCaTruc + ", maBacSi=" + maBacSi + "}";
    }
}
