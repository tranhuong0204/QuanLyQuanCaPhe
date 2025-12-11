package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class HoaDonController {
    @FXML private ScrollPane scrollPane;
    //    @FXML private ListView<SanPham> listHoaDon;
    @FXML private ListView<ItemHoaDon> listHoaDon;
    private ObservableList<ItemHoaDon> dsMon = FXCollections.observableArrayList();
    private VBox selectedBox;

    @FXML private Label tongTienLabel;
    @FXML private Label tienThuaLabel;
    @FXML private TextField tienKhachDuaField;


    private double tongTien = 0;
    // ĐÃ XÓA: private TaiKhoan taiKhoanNhanVien;

    // ĐÃ XÓA: public void setTaiKhoanNhanVien(TaiKhoan tk) { this.taiKhoanNhanVien = tk; }

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

    @FXML
    private void handleThanhToan() {
        try {
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

            // ✅ Lưu vào DB
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                // 1. Insert HOADON
                int maHoaDon = generateMaHoaDon(conn);

                String sqlHoaDon = "INSERT INTO HOADON (maHoaDon, tongKM, tongTien, ngayLap,  maBan, maTaiKhoan, maPT) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psHoaDon = conn.prepareStatement(sqlHoaDon);
                psHoaDon.setInt(1, maHoaDon);
                psHoaDon.setDouble(2, 0); // tongKM
                psHoaDon.setDouble(3, tongTien);
                psHoaDon.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                psHoaDon.setString(5, "b001");

                // DÒNG ĐÃ SỬA: Lấy mã tài khoản từ đối tượng đã lưu trong phiên
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
            }

            tienThuaLabel.setText("Tiền thừa: " + String.format("%.0fđ", tienThua));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thanh toán");
            alert.setHeaderText("Hóa đơn đã được thanh toán");
            alert.setContentText("Tổng tiền: " + String.format("%.0fđ", tongTien) + "\nTiền thừa: " + String.format("%.0fđ", tienThua));
            alert.showAndWait();

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