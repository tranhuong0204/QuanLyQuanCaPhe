
package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.SanPhamDAO;
import com.example.quanlyquancaphe.models.SanPham;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class SanPhamController {

    @FXML
    private ScrollPane scrollPane;

    private final ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();
    private VBox selectedBox;

    @FXML
    public void initialize() {
        if (scrollPane == null) {
            System.err.println("SanPhamController.initialize: scrollPane is null. Check your FXML.");
            return;
        }

        // Kết nối CSDL
        try {
            Connection conn = DatabaseConnection.getConnection();
            SanPhamDAO dao = new SanPhamDAO(conn);
            List<SanPham> list = dao.getAllSanPham();
            danhSachSanPham.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hiển thị bằng TilePane
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(3);

        for (SanPham sp : danhSachSanPham) {
            ImageView img = null;
            if (sp.getHinhAnh() != null) {
                URL imgUrl = getClass().getResource(sp.getHinhAnh());
                if (imgUrl != null) {
                    img = new ImageView(new Image(imgUrl.toExternalForm()));
                    img.setFitWidth(100);
                    img.setFitHeight(100);
                } else {
                    System.err.println("Không tìm thấy ảnh: " + sp.getHinhAnh());
                }
            }
//            URL imgUrl = getClass().getResource(sp.getHinhAnh());
//            if (imgUrl != null) {
//                Image img = new Image(imgUrl.toExternalForm());
//                ImageView imageView = new ImageView(img);
//                imageView.setFitWidth(100);
//                imageView.setFitHeight(100);
//                box.getChildren().add(imageView);
//            } else {
//                System.err.println("Không tìm thấy ảnh: " + sp.getHinhAnh());
//            }

            //Label ten = new Label(sp.getTen());
            //Label gia = new Label(String.format("%.2f", sp.getDonGia()));

            VBox box = new VBox(10);
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().add("product-box");

            if (img != null) {
                img.setFitWidth(120);
                img.setFitHeight(120);
                img.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 0, 2);");
                box.getChildren().add(img);
            }

            Label ten = new Label(sp.getTen());
            ten.getStyleClass().add("product-label");

//            Label gia = new Label(String.format("%.0f đ", sp.getDonGia()));
//            gia.setStyle("-fx-text-fill: #666666;");
//            box.getChildren().addAll(ten, gia);

            if (sp.getGiaKM() != null && sp.getGiaKM() > 0 && sp.getGiaKM() < sp.getDonGia()) {
//                Label giaGoc = new Label(String.format("%.0f đ", sp.getDonGia()));
//                giaGoc.setStyle("-fx-text-fill: #999999; -fx-strikethrough: true;");
                Text giaGoc = new Text(String.format("%.0f đ", sp.getDonGia()));
                giaGoc.setFill(Color.BLACK);
                giaGoc.setStrikethrough(true);

                Label giaKM = new Label(String.format("%.0f đ", sp.getGiaKM()));
                giaKM.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;"); // màu xanh

                // add giá gốc trước, giá KM sau => gốc trên, KM dưới
                box.getChildren().addAll(ten, giaGoc, giaKM);
            } else {
                Label gia = new Label(String.format("%.0f đ", sp.getDonGia()));
                gia.setStyle("-fx-text-fill: #000000;");
                box.getChildren().addAll(ten, gia);
            }



            box.setOnMouseClicked(e -> {
                if (selectedBox != null) {
                    selectedBox.getStyleClass().remove("selected-box");
                }
                box.getStyleClass().add("selected-box");
                selectedBox = box;
                showDetails(sp);
            });


            tilePane.getChildren().add(box);
        }

        scrollPane.setContent(tilePane);
        scrollPane.setFitToWidth(true);
    }

    private void showDetails(SanPham sp) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết sản phẩm");
        alert.setHeaderText(sp.getTen());
        alert.setContentText(
                "Mã: " + sp.getMa() + "\n" +
                        "Giá: " + sp.getDonGia() + "\n" +
                        "Mô tả: " + sp.getMoTa()
        );
        alert.showAndWait();
    }
}
