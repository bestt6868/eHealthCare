# 🏥 HospitalKiosk – Hệ thống cấp phiếu khám bệnh tự động

## Tổng quan

Hệ thống gồm 2 phân hệ:
- **Kiosk (bệnh nhân):** Toàn màn hình, nhập BHYT → chọn khoa → chọn bác sĩ → in phiếu
- **Admin (nhân viên):** Quản lý khoa, bác sĩ, ca trực, hàng chờ

---

## Yêu cầu môi trường

| Phần mềm          | Phiên bản yêu cầu |
|-------------------|-------------------|
| JDK               | 11 trở lên        |
| SQL Server        | 2017 trở lên      |
| MSSQL JDBC Driver | 9.x trở lên       |

---

## Cấu trúc thư mục

```
HospitalKiosk/
├── config.properties              ← cấu hình kết nối DB
├── sql/
│   ├── 01_create_tables.sql       ← tạo schema DB
│   ├── 02_insert_sample_data.sql  ← dữ liệu mẫu
│   └── 03_stored_procedures.sql   ← stored procedures
└── src/main/
    ├── Main.java                  ← entry point
    ├── config/DatabaseConfig.java
    ├── model/*.java               ← 8 entity classes
    ├── dao/*.java                 ← 7 DAO classes
    ├── controller/*.java          ← 6 controller classes
    ├── util/*.java                ← UIConstants, ValidationUtil, PrinterUtil
    └── view/
        ├── kiosk/                 ← 5 files (KioskApp + 4 màn hình)
        └── admin/                 ← 7 files (AdminApp + 6 panels)
```

---

## Hướng dẫn cài đặt

### Bước 1 – Tạo database SQL Server

Chạy lần lượt trong SQL Server Management Studio (SSMS):

```sql
-- 1. Tạo schema
-- Chạy: sql/01_create_tables.sql

-- 2. Chèn dữ liệu mẫu
-- Chạy: sql/02_insert_sample_data.sql

-- 3. Tạo stored procedures (tuỳ chọn)
-- Chạy: sql/03_stored_procedures.sql
```

### Bước 2 – Cấu hình kết nối

Chỉnh sửa file `config.properties`:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=HospitalKioskDB;encrypt=false;trustServerCertificate=true
db.username=sa
db.password=<mật_khẩu_của_bạn>
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Bước 3 – Thêm JDBC Driver

Tải `mssql-jdbc-x.x.x.jre11.jar` từ:
https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server

### Bước 4 – Biên dịch

```bash
# Tập hợp tất cả file .java
find src/main -name "*.java" > sources.txt

# Biên dịch (thêm JDBC driver vào classpath)
javac -encoding UTF-8 -cp ".;mssql-jdbc-x.x.x.jre11.jar" @sources.txt -d out/

# Copy config.properties vào output
cp config.properties out/
```

### Bước 5 – Chạy ứng dụng

```bash
# Chạy phân hệ Kiosk
java -cp "out;mssql-jdbc-x.x.x.jre11.jar" Main kiosk

# Chạy phân hệ Admin
java -cp "out;mssql-jdbc-x.x.x.jre11.jar" Main admin

# Chạy với dialog chọn phân hệ
java -cp "out;mssql-jdbc-x.x.x.jre11.jar" Main
```

> **Linux/macOS:** Dùng `:` thay vì `;` làm dấu phân cách classpath.

---

## Tài khoản mẫu

| Loại             | Thông tin                                    |
|------------------|----------------------------------------------|
| **Admin**        | Mã NV: `1` / Mật khẩu: `Admin@123`          |
| **BHYT hợp lệ**  | `DN123456789012345` (Nguyễn Thị Lan)         |
| **BHYT hợp lệ**  | `HN987654321098765` (Trần Văn Bảo)           |
| **BHYT hết hạn** | `SG111222333444555` (Lê Minh Châu – hết hạn) |

---

## Checklist kiểm thử

### Phân hệ Kiosk
- [ ] Nhập BHYT 14 ký tự → nút XÁC NHẬN bị disable
- [ ] Nhập BHYT không tồn tại → thông báo lỗi đỏ
- [ ] Nhập BHYT hết hạn (`SG111222333444555`) → "Thẻ BHYT đã hết hạn"
- [ ] Nhập BHYT hợp lệ → hiện tên BN, chuyển màn hình chọn khoa
- [ ] Chọn khoa → chỉ hiển thị bác sĩ của khoa đó
- [ ] Nhấn BỎ QUA → hệ thống tự chọn bác sĩ ít người chờ
- [ ] Sau in phiếu 3 giây → tự reset về màn hình nhập BHYT

### Phân hệ Admin
- [ ] Đăng nhập sai 3 lần → tài khoản bị khóa
- [ ] Đăng nhập đúng → vào màn hình quản lý
- [ ] Thêm khoa trùng tên → thông báo lỗi
- [ ] Tắt bác sĩ → không còn hiện trong Kiosk
- [ ] Thêm ca trực bị xung đột giờ → cảnh báo tên bác sĩ bị xung đột
- [ ] Reset hàng chờ → tất cả số người chờ về 0

---

## Lưu ý nghiệp vụ quan trọng

1. **Mã BHYT** là chuỗi 15 ký tự (`VARCHAR(15)`), không phải số nguyên.
2. **Số thứ tự** độc lập theo từng bác sĩ mỗi ngày.
3. **Khoa** chỉ hiện trong Kiosk khi có ít nhất 1 bác sĩ đang hoạt động.
4. **Điều phối tự động:** Nhiều bác sĩ cùng số người chờ → chọn ngẫu nhiên (`ORDER BY NEWID()`).
5. **Máy in:** Nếu không có máy in thật, `PrinterUtil` tự động hiển thị JDialog mô phỏng.
6. **Bảo mật:** Mật khẩu được hash SHA-256, không lưu plain text.
