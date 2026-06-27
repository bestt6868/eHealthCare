package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích kiểm tra và xác thực dữ liệu đầu vào.
 */
public class ValidationUtil {

    // Constructor private – không cho phép tạo instance
    private ValidationUtil() {}

    /**
     * Kiểm tra mã BHYT hợp lệ: đúng 15 ký tự, không null, không khoảng trắng.
     * @param maBHYT mã bảo hiểm y tế cần kiểm tra
     * @return true nếu hợp lệ
     */
    public static boolean laMaBHYTHopLe(String maBHYT) {
        if (maBHYT == null) return false;
        String maTrimmed = maBHYT.trim();
        return maTrimmed.length() == UIConstants.MA_BHYT_LENGTH
               && !maTrimmed.contains(" ");
    }

    /**
     * Kiểm tra mật khẩu hợp lệ: không null, không rỗng.
     * @param matKhau mật khẩu cần kiểm tra
     * @return true nếu hợp lệ
     */
    public static boolean laMatKhauHopLe(String matKhau) {
        return matKhau != null && !matKhau.trim().isEmpty();
    }

    /**
     * Mã hóa chuỗi đầu vào bằng thuật toán SHA-256.
     * Dùng để hash mật khẩu trước khi lưu hoặc so sánh với DB.
     * @param input chuỗi cần mã hóa
     * @return chuỗi hex 64 ký tự đại diện cho giá trị hash
     * @throws RuntimeException nếu thuật toán SHA-256 không khả dụng
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest  = MessageDigest.getInstance("SHA-256");
            byte[]        hashBytes = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán SHA-256 không khả dụng!", e);
        }
    }

    /**
     * Chuyển mảng byte thành chuỗi hex.
     */
    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Kiểm tra chuỗi có rỗng hoặc null không.
     * @param str chuỗi cần kiểm tra
     * @return true nếu rỗng hoặc null
     */
    public static boolean laRong(String str) {
        return str == null || str.trim().isEmpty();
    }
}
