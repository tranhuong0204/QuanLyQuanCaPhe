package com.example.quanlyquancaphe.controllers.employee;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class SanPhamController {
    @FXML
    private VBox container;

    @FXML
    public void initialize() {
        hienThiMon(container);
    }
    // Tạm tạo dữ liệu mẫu
    public void hienThiMon(VBox container) {
        // Tạm tạo dữ liệu mẫu
        String tenMon = "Cà phê sữa";
        int giaMon = 25000;
        String duongDanAnh = "/com/example/quanlyquancaphe/images/hinhnencafe.jpg";

        // Tạo ImageView
        ImageView imageView = new ImageView(new Image(duongDanAnh));
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        // Tạo các Label
        Label tenLabel = new Label(tenMon);
        Label giaLabel = new Label(String.format("%,d₫", giaMon));

        // Tạo VBox chứa món
        VBox itemBox = new VBox(imageView, tenLabel, giaLabel);
        itemBox.setSpacing(5);
        itemBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        itemBox.setAlignment(Pos.CENTER);

        // Thêm vào container (VBox hoặc FlowPane)
        container.getChildren().add(itemBox);
    }

}
