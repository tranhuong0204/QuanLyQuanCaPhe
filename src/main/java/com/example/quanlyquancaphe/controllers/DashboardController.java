package com.example.quanlyquancaphe.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML private Label taiKhoanlb;
    @FXML private Label thongKelb;
    @FXML private Label KHUYENMAILABEL; // label "KHUYẾN MÃI" từ FXML
    @FXML private StackPane contentPane;

    private List<Label> labelList;

    @FXML
    public void initialize() {
        // Gom các label, bỏ qua những label chưa được inject (null)
        labelList = new ArrayList<>();
        if (taiKhoanlb != null) labelList.add(taiKhoanlb);
        if (thongKelb != null) labelList.add(thongKelb);
        if (KHUYENMAILABEL != null) labelList.add(KHUYENMAILABEL);

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
                    loadIntoContent("/com/example/quanlyquancaphe/ThongKe.fxml");
                } else if (label == KHUYENMAILABEL) {
                    loadIntoContent("/com/example/quanlyquancaphe/KhuyenMai.fxml");
                }
            });
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/ThongKe.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
