package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Lớp cấu hình kết nối cơ sở dữ liệu.
 * Đọc thông tin kết nối từ file config.properties, không hardcode trong code.
 */
public class DatabaseConfig {

    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static String dbDriver;

    // Khối static: đọc config khi class được load
    static {
        docCauHinhKetNoi();
    }

    /**
     * Đọc file config.properties từ classpath và nạp thông số kết nối DB.
     */
    private static void docCauHinhKetNoi() {
        Properties props = new Properties();
        try (InputStream is = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("config/config.properties")) {

            if (is == null) {
                throw new RuntimeException(
                    "Không tìm thấy file config.properties trong classpath!");
            }
            props.load(is);
            dbDriver   = props.getProperty("db.driver");
            dbUrl      = props.getProperty("db.url");
            dbUsername = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");

            Class.forName(dbDriver);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file config.properties: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy JDBC driver: " + dbDriver, e);
        }
    }

    /**
     * Tạo và trả về kết nối đến cơ sở dữ liệu SQL Server.
     * @return Connection đến HospitalKioskDB
     * @throws RuntimeException nếu kết nối thất bại
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Không thể kết nối đến cơ sở dữ liệu. Kiểm tra lại config.properties.\n"
                + "Chi tiết lỗi: " + e.getMessage(), e);
        }
    }
}
