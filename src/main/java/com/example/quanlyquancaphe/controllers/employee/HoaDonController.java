package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage; // Import Stage

import java.io.FileWriter; // Import FileWriter cho chức năng in file
import java.io.IOException; // Import IOException
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class HoaDonController {
    @FXML private ScrollPane scrollPane;
    @FXML private ListView<ItemHoaDon> listHoaDon;
    private ObservableList<ItemHoaDon> dsMon = FXCollections.observableArrayList();
    private VBox selectedBox;

    @FXML private Label tongTienLabel;
    @FXML private Label tienThuaLabel;
    @FXML private TextField tienKhachDuaField;
    @FXML private Label lblThongTinBan; // Đổi tên từ lbThongTinBan sang lblThongTinBan

    private double tongTien = 0;
    private Ban banHienTai;
    private BanDAO banDAO = new BanDAO();
    private ChonBanController parentController;

    // PHƯƠNG THỨC SETTER CHO PARENT
    public void setParentController(ChonBanController parent) {
        this.parentController = parent;
    }

    // PHƯƠNG THỨC NHẬN DỮ LIỆU BÀN
    public void setData(Ban selectedBan) {
        this.banHienTai = selectedBan;

        if (banHienTai != null) {
            // Cập nhật Label trên giao diện
            if (lblThongTinBan != null) {
                lblThongTinBan.setText("Lập Hóa Đơn cho: " + banHienTai.getMaBan() + " (" + banHienTai.getViTri() + ")");
            } else {
                System.err.println("Lỗi: lblThongTinBan chưa được ánh xạ trong FXML.");
            }

            System.out.println("Hóa đơn được lập cho Bàn: " + banHienTai.getMaBan());
            // TODO: Nếu bàn có trạng thái "Có khách", load hóa đơn cũ.
        }
    }

    @FXML
    public void initialize() {
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(3);

        try {
            Connection conn = DatabaseConnection.getConnection();
            SanPhamDAO dao = new SanPhamDAO(conn);
            List<SanPham> danhSachSanPham = dao.getAllSanPham();

            for (SanPham sp : danhSachSanPham) {
                VBox box = createSanPhamBox(sp);
                box.setOnMouseClicked(e -> themVaoHoaDon(sp));
                tilePane.getChildren().add(box);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // === LOGIC XỬ LÝ SỰ KIỆN ĐÓNG CỬA SỔ ===
        Platform.runLater(() -> {
            Stage stage = (Stage) scrollPane.getScene().getWindow();

            stage.setOnCloseRequest(event -> {

                // 1. Nếu danh sách món ĐÃ CÓ (đã gọi món), CHẶN đóng cửa sổ
                if (!dsMon.isEmpty()) {
                    event.consume(); // Ngăn chặn sự kiện đóng cửa sổ mặc định

                    new Alert(Alert.AlertType.WARNING,
                            "Bạn đã thêm món vào hóa đơn. Vui lòng sử dụng nút 'Thanh toán' để kết thúc giao dịch.").show();

                } else {
                    // 2. Nếu danh sách món RỖNG (chưa gọi món nào), CHO PHÉP đóng

                    // === LOGIC QUAN TRỌNG: HOÀN TÁC TRẠNG THÁI BÀN ===
                    if (banHienTai != null && banDAO != null && parentController != null) {
                        // Cập nhật trạng thái bàn trong DB về "Trống"
                        if (banDAO.updateTrangThai(banHienTai.getMaBan(), "Trống")) {

                            // Cập nhật đối tượng Ban trong bộ nhớ và giao diện chính
                            banHienTai.setTrangThai("Trống");
                            parentController.loadData();
                            System.out.println("DEBUG: Đã hoàn tác Bàn " + banHienTai.getMaBan() + " về trạng thái 'Trống'.");

                        } else {
                            System.err.println("LỖI: Không thể hoàn tác trạng thái bàn trong DB.");
                        }
                    }

                    // Do không gọi event.consume(), cửa sổ sẽ đóng bình thường.
                }
            });
        });

        scrollPane.setContent(tilePane);
        scrollPane.setFitToWidth(true);

        listHoaDon.setItems(dsMon);
        listHoaDon.setCellFactory(param -> new ListCell<ItemHoaDon>() {
            @Override
            protected void updateItem(ItemHoaDon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    SanPham sp = item.getSanPham();

                    Label ten = new Label(sp.getTen());
                    ten.setPrefWidth(150);
                    Label gia = new Label(String.format("%.0fđ", sp.getDonGia()));
                    gia.setPrefWidth(80);

                    TextField soLuongField = new TextField(String.valueOf(item.getSoLuong()));
                    soLuongField.setPrefWidth(40);
                    soLuongField.setAlignment(Pos.CENTER);
                    soLuongField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

                    Button btnTang = new Button("+");
                    Button btnGiam = new Button("-");
                    btnTang.setOnAction(e -> {
                        item.tangSoLuong();
                        soLuongField.setText(String.valueOf(item.getSoLuong()));
                        capNhatTongTien();
                    });
                    btnGiam.setOnAction(e -> {
                        item.giamSoLuong();
                        soLuongField.setText(String.valueOf(item.getSoLuong()));
                        capNhatTongTien();
                    });

                    soLuongField.textProperty().addListener((obs, oldVal, newVal) -> {
                        try {
                            int sl = Integer.parseInt(newVal);
                            if (sl > 0) {
                                item.setSoLuong(sl);
                                capNhatTongTien();
                            }
                        } catch (NumberFormatException ignored) {}
                    });


                    Button btnXoa = new Button("❌");
                    btnXoa.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
                    btnXoa.setOnAction(e -> {
                        dsMon.remove(item);
                        capNhatTongTien();
                    });

                    // Gom nhóm số lượng
                    HBox soLuongBox = new HBox(5, btnGiam, soLuongField, btnTang);
                    soLuongBox.setAlignment(Pos.CENTER);

                    // Gom tất cả thành một hàng
                    HBox box = new HBox(20, ten, gia, soLuongBox, btnXoa);
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setPadding(new Insets(5));
                    setGraphic(box);
                }
            }
        });

    }

    private VBox createSanPhamBox(SanPham sp) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("product-box");
        ImageView img = null;
        URL imgUrl = getClass().getResource(sp.getHinhAnh());
        if (imgUrl != null) {
            img = new ImageView(new Image(imgUrl.toExternalForm()));
            img.setFitWidth(100);
            img.setFitHeight(100);
            img.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 0, 2);");
            box.getChildren().add(img);
        }

        Label ten = new Label(sp.getTen());
        Label gia = new Label(String.format("%.0fđ", sp.getDonGia()));

        box.getChildren().addAll(ten, gia);

        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        return box;
    }

    private void themVaoHoaDon(SanPham sp) {
        for (ItemHoaDon item : dsMon) {
            if (item.getSanPham().getMa() == sp.getMa()) {
                item.tangSoLuong();
                listHoaDon.refresh();
                capNhatTongTien();
                return;
            }
        }
        dsMon.add(new ItemHoaDon(sp, 1));
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        tongTien = 0;
        for (ItemHoaDon item : dsMon) {
            tongTien += item.getSanPham().getDonGia() * item.getSoLuong();
        }
        tongTienLabel.setText("Tổng: " + String.format("%.0fđ", tongTien));
    }


    private int generateMaHoaDon(Connection conn) throws SQLException {
        String sql = "SELECT ISNULL(MAX(maHoaDon), 0) FROM HOADON";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1; // nếu bảng rỗng thì bắt đầu từ 1
    }

    // Phương thức in hóa đơn ra file TXT
    private void printHoaDonToTxt(int maHoaDon, double tienKhachDua, double tienThua) {
        String maNhanVien = (TaiKhoan.isUserLoggedIn() && TaiKhoan.getUserLoggedIn().getMaTaiKhoan() != null) ? TaiKhoan.getUserLoggedIn().getMaTaiKhoan() : "UNKNOWN";

        try (FileWriter writer = new FileWriter("HoaDon_" + maHoaDon + ".txt")) {
            writer.write("================= COFFEE SHOP =================\n");
            writer.write("          HOÁ ĐƠN THANH TOÁN (BILL)\n");
            writer.write("Mã HĐ: " + maHoaDon + "\n");
            writer.write("Bàn: " + banHienTai.getMaBan() + " - NV: " + maNhanVien + "\n");
            writer.write("Ngày: " + LocalDate.now() + "\n");
            writer.write("-----------------------------------------------\n");
            writer.write(String.format("%-15s %-5s %10s\n", "Tên Món", "SL", "Thành Tiền"));
            writer.write("-----------------------------------------------\n");

            for (ItemHoaDon item : listHoaDon.getItems()) {
                double thanhTien = item.getSanPham().getDonGia() * item.getSoLuong();
                writer.write(String.format("%-15s %-5d %10.0fđ\n",
                        item.getSanPham().getTen(),
                        item.getSoLuong(),
                        thanhTien));
            }
            writer.write("-----------------------------------------------\n");
            writer.write(String.format("%-21s %15.0fđ\n", "TỔNG CỘNG:", tongTien));
            writer.write(String.format("%-21s %15.0fđ\n", "KHÁCH ĐƯA:", tienKhachDua));
            writer.write(String.format("%-21s %15.0fđ\n", "TIỀN THỪA:", tienThua));
            writer.write("===============================================\n");
            System.out.println("DEBUG: Đã in hóa đơn " + maHoaDon + " ra file.");
        } catch (IOException e) {
            System.err.println("Lỗi khi in hóa đơn ra file TXT: " + e.getMessage());
        }
    }


    @FXML
    private void handleThanhToan() {

        // Cần phải khai báo biến Stage ở ngoài để đóng cửa sổ.
        Stage currentStage = (Stage) tienKhachDuaField.getScene().getWindow();

        Connection conn = null; // Khai báo Connection ở phạm vi cao hơn

        try {

            // 0. KIỂM TRA TÍNH TOÀN VẸN DỮ LIỆU
            if (banHienTai == null || banHienTai.getMaBan().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: Không tìm thấy thông tin bàn để lập hóa đơn.").showAndWait();
                return;
            }

            double tienKhachDua = Double.parseDouble(tienKhachDuaField.getText());
            double tienThua = tienKhachDua - tongTien;

            if (tienThua < 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Thiếu tiền");
                alert.setHeaderText("Khách đưa chưa đủ tiền");
                alert.setContentText("Khách cần đưa thêm: " + String.format("%.0fđ", -tienThua));
                alert.showAndWait();
                return;
            }

            // KIỂM TRA PHIÊN ĐĂNG NHẬP
            if (!TaiKhoan.isUserLoggedIn() || TaiKhoan.getUserLoggedIn().getMaTaiKhoan() == null) {
                new Alert(Alert.AlertType.ERROR, "Không tìm thấy thông tin nhân viên lập hóa đơn. Vui lòng đăng nhập lại!").showAndWait();
                return;
            }

            // KIỂM TRA HÀNG HÓA
            if (dsMon.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ít nhất một món để thanh toán.").showAndWait();
                return;
            }


            // ✅ Xử lý DB
            try {
                conn = DatabaseConnection.getConnection(); // Gán giá trị cho conn
                conn.setAutoCommit(false);

                // 1. Insert HOADON
                int maHoaDon = generateMaHoaDon(conn);

                String sqlHoaDon = "INSERT INTO HOADON (maHoaDon, tongKM, tongTien, ngayLap,  maBan, maTaiKhoan, maPT) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psHoaDon = conn.prepareStatement(sqlHoaDon);
                psHoaDon.setInt(1, maHoaDon);
                psHoaDon.setDouble(2, 0); // tongKM
                psHoaDon.setDouble(3, tongTien);
                psHoaDon.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

                psHoaDon.setString(5, banHienTai.getMaBan());

                psHoaDon.setString(6, TaiKhoan.getUserLoggedIn().getMaTaiKhoan());

                psHoaDon.setInt(7, 1);
                psHoaDon.executeUpdate();

                // 2. Insert CHITIETHOADON
                String sqlCTHD = "INSERT INTO CHITIETHOADON (maMon, maHoaDon, soLuong) VALUES (?, ?, ?)";
                PreparedStatement psCTHD = conn.prepareStatement(sqlCTHD);

                for (ItemHoaDon item : listHoaDon.getItems()) {
                    SanPham sp = item.getSanPham();
                    int soLuong = item.getSoLuong();
                    psCTHD.setString(1, sp.getMa());
                    psCTHD.setInt(2, maHoaDon);
                    psCTHD.setInt(3, soLuong);
                    psCTHD.addBatch();
                }
                psCTHD.executeBatch();

                conn.commit();

                // === XỬ LÝ SAU KHI THÀNH CÔNG ===

                // 3. Cập nhật Trạng Thái Bàn về "Trống"
                banDAO.updateTrangThai(banHienTai.getMaBan(), "Trống");

                // 4. Cập nhật dữ liệu trên màn hình chọn bàn (Parent Controller)
                if (parentController != null) {
                    parentController.loadData();
                }

                // 5. In hóa đơn ra file TXT
                printHoaDonToTxt(maHoaDon, tienKhachDua, tienThua);

                // 6. Thông báo và đóng cửa sổ
                tienThuaLabel.setText("Tiền thừa: " + String.format("%.0fđ", tienThua));
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Thanh toán");
                infoAlert.setHeaderText("Hóa đơn đã được thanh toán");
                infoAlert.setContentText("Tổng tiền: " + String.format("%.0fđ", tongTien) + "\nTiền thừa: " + String.format("%.0fđ", tienThua));
                infoAlert.showAndWait();

                currentStage.close(); // Đóng cửa sổ chỉ khi thanh toán thành công

            } catch (Exception e) {
                // Xử lý Rollback nếu có lỗi DB
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                // Đóng kết nối (Đảm bảo an toàn)
                if (conn != null) {
                    conn.close();
                }
            }

            listHoaDon.getItems().clear();
            tongTien = 0;
            tongTienLabel.setText("Tổng: 0đ");
            tienKhachDuaField.clear();
            tienThuaLabel.setText("Tiền thừa: 0đ");

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("Tiền khách đưa không hợp lệ");
            alert.setContentText("Vui lòng nhập số tiền hợp lệ.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu hóa đơn");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }
}