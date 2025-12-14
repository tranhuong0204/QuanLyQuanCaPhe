package com.example.quanlyquancaphe.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrangChuController {
    @FXML private Label taiKhoanlb, monlb;
    @FXML private Label thongKelb;
    @FXML private Label KHUYENMAILABEL; // label "KHUYẾN MÃI" từ FXML
    @FXML private StackPane contentPane;
    @FXML private Label quanLyBan;
    @FXML private Label quanLyHD;
    @FXML private Label thoatlb;


    private List<Label> labelList;
    private void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Gom các label, bỏ qua những label chưa được inject (null)
        labelList = new ArrayList<>();
        if (taiKhoanlb != null) labelList.add(taiKhoanlb);
        if (thongKelb != null) labelList.add(thongKelb);
        if (KHUYENMAILABEL != null) labelList.add(KHUYENMAILABEL);
        if (quanLyBan != null) {
            labelList.add(quanLyBan);
        }
        labelList.add(monlb);
        labelList.add(quanLyHD);
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
                if (label == thongKelb) {
                    loadIntoContent("/com/example/quanlyquancaphe/adminView/ThongKe.fxml");
                } else if (label == KHUYENMAILABEL) {
                    loadIntoContent("/com/example/quanlyquancaphe/adminView/KhuyenMai.fxml");
                }
                else if (label == monlb) {
                    loadIntoContent("/com/example/quanlyquancaphe/adminView/QLSP.fxml");
                }
                else if (label == quanLyBan) {
                    loadIntoContent("/com/example/quanlyquancaphe/adminView/QuanLyBan.fxml");}

                else if(label == taiKhoanlb){
                    loadIntoContent("/com/example/quanlyquancaphe/adminView/QuanLyTaiKhoan.fxml");
                    }
                else if(label == quanLyHD){
                    loadIntoContent("/com/example/quanlyquancaphe/adminView/QLHD.fxml");
                }

                else if (label == thoatlb) {
                    // 1. Tạo hộp thoại xác nhận
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận đăng xuất");
                    alert.setHeaderText(null);
                    alert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

                    // Đặt lại các nút để hiển thị bằng tiếng Việt (tùy chọn)
                    ButtonType buttonTypeYes = new ButtonType("Đồng ý", ButtonBar.ButtonData.OK_DONE);
                    ButtonType buttonTypeNo = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    // 2. Chờ người dùng phản hồi
                    Optional<ButtonType> result = alert.showAndWait();

                    // 3. Xử lý phản hồi
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        // Người dùng chọn 'Đồng ý' (thoát)
                        try {

                            Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.fxml"));
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.css").toExternalForm());

                            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            currentStage.close();

                            Stage newStage = new Stage();
                            newStage.setScene(scene);
                            newStage.centerOnScreen();
                            newStage.show();

                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }

                }
            });
        }

        if (thongKelb != null) {
            for (Label l : labelList) {
                l.getStyleClass().remove("label-selected");
            }
            thongKelb.getStyleClass().add("label-selected");
            loadIntoContent("/com/example/quanlyquancaphe/adminView/ThongKe.fxml");
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
