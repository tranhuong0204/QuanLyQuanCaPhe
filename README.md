
<h1 align="center">
  <br>
  <a href="https://github.com/tranhuong0204/QuanLyQuanCaPhe/"><img src="https://vps029.manageserver.in/menu/wp-content/uploads/2024/04/Hot-Coffee.png" alt="QCPN" width="400" height="300"></a>
  <br>
  QuanCaPheNho - Hệ Thống Quản Lý Quán Cà Phê
  <br>
</h1>

<h4 align="center">Ứng dụng quản lý quán cà phê trên desktop được xây dựng bằng Java, JavaFX và Maven. Hỗ trợ quản lý sản phẩm, hóa đơn, khuyến mãi, nhân viên và thống kê doanh thu.</h4>

<p align="center">
  <a href="#tính-năng-chính">Tính năng</a> •
  <a href="#công-nghệ-sử-dụng">Công nghệ</a> •
  <a href="#cài-đặt-và-chạy">Cài đặt</a> •
  <a href="#cấu-trúc-dự-án">Cấu trúc</a> •
  <a href="#thành-viên">Thành viên</a>
</p>

---

## Tính năng chính

### 🔐 Xác thực & Phân quyền
- Đăng nhập với tài khoản Admin hoặc Nhân viên
- Phân quyền truy cập theo vai trò (Quản lý / Nhân viên)

### 📦 Quản lý Sản phẩm (QLSP)
- Thêm, sửa, xóa sản phẩm (thức uống)
- Upload hình ảnh sản phẩm
- Tìm kiếm sản phẩm theo tên
- Hiển thị danh sách sản phẩm dạng bảng với hình ảnh

### 🪑 Quản lý Bàn
- Thêm, sửa, xóa bàn
- Phân loại theo vị trí (Trong nhà / Ngoài trời)
- Theo dõi trạng thái bàn (Trống / Có khách)
- Thiết lập số ghế và ghi chú

### 🎟️ Quản lý Khuyến mãi
- Tạo chương trình khuyến mãi với phần trăm giảm giá
- Thiết lập thời gian áp dụng (ngày bắt đầu - kết thúc)
- **Liên kết khuyến mãi với sản phẩm cụ thể (N-N)** thông qua bảng trung gian `MON_KHUYENMAI`
- Tìm kiếm khuyến mãi theo tên hoặc phần trăm

### 🧾 Quản lý Hóa đơn / Gọi món
- Giao diện chọn món trực quan với hình ảnh
- Thêm/bớt số lượng món trong hóa đơn
- Tính tổng tiền tự động
- Tính tiền thừa khi khách thanh toán
- Lưu hóa đơn và chi tiết hóa đơn vào database

### 👥 Quản lý Tài khoản
- Thêm, sửa, xóa tài khoản nhân viên
- Phân loại chức vụ (Quản lý / Nhân viên)
- Tìm kiếm tài khoản theo tên hoặc chức vụ

### 📊 Thống kê & Báo cáo
- Biểu đồ doanh thu theo năm
- Biểu đồ doanh thu theo tháng (chọn năm cụ thể)
- Hiển thị dạng Area Chart trực quan

---

## Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ | Java 25 |
| Giao diện | JavaFX 21.0.6, FXML, CSS |
| Database | Microsoft SQL Server |
| JDBC Driver | mssql-jdbc 12.4.2 |
| Build Tool | Maven |
| Testing | JUnit 5 |
| IDE | IntelliJ IDEA |

---

## Yêu cầu hệ thống

- **JDK:** Java 25 trở lên
- **JavaFX:** 21.0.6+
- **Maven:** 3.8+
- **Database:** SQL Server (cần cấu hình connection string trong `DatabaseConnection.java`)
- **OS:** Windows 10/11

---

## Cài đặt và chạy

### Clone dự án
```bash
git clone https://github.com/tranhuong0204/QuanLyQuanCaPhe.git
cd QuanLyQuanCaPhe
```

### Cấu hình Database
1. Tạo database trên SQL Server
2. Chạy các script SQL để tạo bảng (MON, BAN, HOADON, KHUYENMAI, MON_KHUYENMAI, TAIKHOAN, ...)
3. Cập nhật connection string trong file `DatabaseConnection.java`

### Build và chạy
```bash
# Build project
mvn clean package

# Chạy ứng dụng
mvn javafx:run

# Hoặc chạy từ IntelliJ IDEA: Run main class `com.example.quanlyquancaphe.Launcher`
```

---

## Cấu trúc dự án

```
QuanLyQuanCaPhe/
├── src/main/java/com/example/quanlyquancaphe/
│   ├── Launcher.java                    # Entry point
│   ├── controllers/
│   │   ├── admin/                       # Controllers cho Admin
│   │   │   ├── DangNhapController.java
│   │   │   ├── TrangChuController.java
│   │   │   ├── QLSPController.java      # Quản lý sản phẩm
│   │   │   ├── QuanLyBanController.java # Quản lý bàn
│   │   │   ├── KhuyenMaiController.java # Quản lý khuyến mãi
│   │   │   ├── TaiKhoanController.java  # Quản lý tài khoản
│   │   │   ├── ThongKeController.java   # Thống kê
│   │   │   └── ...
│   │   └── employee/                    # Controllers cho Nhân viên
│   │       ├── TrangChuController.java
│   │       ├── HoaDonController.java    # Tạo hóa đơn
│   │       ├── ChonBanController.java   # Chọn bàn
│   │       └── ...
│   ├── models/                          # Data Access Objects & Models
│   │   ├── DatabaseConnection.java
│   │   ├── SanPham.java, SanPhamDAO.java
│   │   ├── Ban.java, BanDAO.java
│   │   ├── KhuyenMai.java, KhuyenMaiDAO.java
│   │   ├── MonKhuyenMaiDAO.java         # DAO cho bảng trung gian N-N
│   │   ├── HoaDon.java, HoaDonDAO.java
│   │   ├── TaiKhoan.java, TaiKhoanDAO.java
│   │   └── ...
│   └── services/
│       └── DangNhapService.java
├── src/main/resources/com/example/quanlyquancaphe/
│   ├── DangNhap.fxml, DangNhap.css
│   ├── adminView/                       # FXML + CSS cho Admin
│   ├── employeeView/                    # FXML + CSS cho Nhân viên
│   └── images/                          # Hình ảnh sản phẩm
└── pom.xml
```

---

## Screenshots

*(Thêm screenshots của ứng dụng tại đây)*

---

## Thành viên

Dự án được phát triển bởi:

- [Thanh Hương](https://github.com/tranhuong0204)
- [Nguyễn Khôi](https://github.com/sniknerduke)
- ntngochuyen
- ngdhiep8905

---

## Giấy phép

Dự án được phát triển cho mục đích học tập.





