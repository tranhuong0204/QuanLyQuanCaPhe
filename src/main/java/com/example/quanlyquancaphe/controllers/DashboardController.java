package com.example.quanlyquancaphe.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.net.URL;

public class DashboardController {
    @FXML
    private StackPane contentPane;

    @FXML
    private void hienThongKe(ActionEvent event) {
        System.out.println("Đã nhấn vào SẢN PHẨM");
    }

    @FXML
    private void hienKhuyenMai(ActionEvent event) {
        showInContent("/com/example/quanlyquancaphe/KhuyenMai.fxml");
    }

    private void showInContent(String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new IllegalStateException("Không tìm thấy FXML: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            // Đặt view lấp đầy vùng contentPane
            if (view instanceof Region region) {
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                region.prefWidthProperty().bind(contentPane.widthProperty());
                region.prefHeightProperty().bind(contentPane.heightProperty());
            }
            StackPane.setAlignment(view, Pos.CENTER);
            contentPane.getChildren().setAll(view);
        } catch (IOException | IllegalStateException e) {
            showErrorDialog("Lỗi khi tải giao diện", e);
        }
    }

    private void showErrorDialog(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(15);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
}
