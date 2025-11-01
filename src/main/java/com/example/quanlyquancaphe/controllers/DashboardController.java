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
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML
    private Label taiKhoanlb, thongKelb;
    @FXML private StackPane contentPane;

    private List<Label> labelList;

    @FXML
    public void initialize() {
        labelList = Arrays.asList(taiKhoanlb, thongKelb);

        for (Label label : labelList) {
            label.getStyleClass().add("label-custom");

            label.setOnMouseClicked(event -> {
                for (Label l : labelList) {
                    l.getStyleClass().remove("label-selected");
                }
                label.getStyleClass().add("label-selected");
                // 👉 Load nội dung tương ứng
                if (label == thongKelb) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/quanlyquancaphe/ThongKe.fxml"));
                        Node thongKeView = loader.load();
                        contentPane.getChildren().setAll(thongKeView);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    @FXML
    private void hienThongKe(MouseEvent event) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/ThongKe.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //mark
        Scene scene = new Scene(root);
//                scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/TrangChu.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
