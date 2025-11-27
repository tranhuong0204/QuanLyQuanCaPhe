package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.SanPhamDAO;
import com.example.quanlyquancaphe.models.SanPham;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class HoaDonController {
    @FXML private ScrollPane scrollPane;
    @FXML private ListView<String> listHoaDon;
    @FXML private Label tongTienLabel;
    @FXML private Label tienThuaLabel;
    @FXML private TextField tienKhachDuaField;


    private double tongTien = 0;

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
    }

    private VBox createSanPhamBox(SanPham sp) {
        ImageView img = null;
        URL imgUrl = getClass().getResource(sp.getHinhAnh());
        if (imgUrl != null) {
            img = new ImageView(new Image(imgUrl.toExternalForm()));
            img.setFitWidth(100);
            img.setFitHeight(100);
        }

        Label ten = new Label(sp.getTen());
        Label gia = new Label(String.format("%.0fđ", sp.getDonGia()));

        VBox box = new VBox(5);
        if (img != null) box.getChildren().add(img);
        box.getChildren().addAll(ten, gia);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        return box;
    }

    private void themVaoHoaDon(SanPham sp) {
        listHoaDon.getItems().add(sp.getTen() + " - " + String.format("%.0fđ", sp.getDonGia()));
        tongTien += sp.getDonGia();
        tongTienLabel.setText("Tổng: " + String.format("%.0fđ", tongTien));
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
        }
    }
}
