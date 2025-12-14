package com.example.quanlyquancaphe.controllers.employee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Import cần thiết
import javafx.scene.control.ButtonBar; // Import cần thiết
import javafx.scene.control.ButtonType; // Import cần thiết
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Import cần thiết

public class TrangChuController {
    @FXML private Label banlb;
    @FXML private Label monlb;
    @FXML private Label KHUYENMAILABEL;
    @FXML private Label thoatlb;


    @FXML private StackPane contentPane;

    private List<Label> labelList;

    @FXML
    public void initialize() {
        labelList = new ArrayList<>();
        if (banlb != null) labelList.add(banlb);
        if (monlb != null) labelList.add(monlb);
        if (KHUYENMAILABEL != null) labelList.add(KHUYENMAILABEL);

        labelList.add(thoatlb);

        for (Label label : labelList) {
            label.getStyleClass().add("label-custom");
            label.setStyle("-fx-cursor: hand;");

            label.setOnMouseClicked(event -> {
                for (Label l : labelList) {
                    l.getStyleClass().remove("label-selected");
                }
                label.getStyleClass().add("label-selected");

                // Nạp nội dung tương ứng vào contentPane
                if (label == monlb) {
                    loadIntoContent("/com/example/quanlyquancaphe/employeeView/SanPham.fxml");
                } else if (label == KHUYENMAILABEL) {
                    loadIntoContent("/com/example/quanlyquancaphe/employeeView/KhuyenMai.fxml");
                } else if (label == banlb) {
                    loadIntoContent("/com/example/quanlyquancaphe/employeeView/ChonBan.fxml");
                } else if (label == thoatlb) {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận đăng xuất");
                    alert.setHeaderText(null); // Không có tiêu đề phụ
                    alert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

                    ButtonType buttonTypeYes = new ButtonType("Đồng ý", ButtonBar.ButtonData.OK_DONE);
                    ButtonType buttonTypeNo = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonTypeYes) {

                        try {

                            Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.fxml"));
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.css").toExternalForm());

                            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            currentStage.close();

                            Stage newStage = new Stage();
                            newStage.setScene(scene);
                            newStage.centerOnScreen(); // căn giữa cửa sổ
                            newStage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (result.isPresent() && result.get() == buttonTypeNo) {

                        label.getStyleClass().remove("label-selected");

                        banlb.getStyleClass().add("label-selected"); // Hoặc nút mặc định
                        loadIntoContent("/com/example/quanlyquancaphe/employeeView/ChonBan.fxml");
                    }
                }
            });
        }

        if (banlb != null) {
            for (Label l : labelList) {
                l.getStyleClass().remove("label-selected");
            }
            banlb.getStyleClass().add("label-selected");
            loadIntoContent("/com/example/quanlyquancaphe/employeeView/ChonBan.fxml");
        }
    }

    private void loadIntoContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            if (contentPane != null) {
                contentPane.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void hienThongKe(MouseEvent event) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/adminView/ThongKe.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}