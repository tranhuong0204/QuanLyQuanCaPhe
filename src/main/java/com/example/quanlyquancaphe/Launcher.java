package com.example.quanlyquancaphe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.fxml"));
//        primaryStage.setTitle("Đăng Nhập");
//        primaryStage.setScene(new Scene(root));hỏi
//        primaryStage.show();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/DangNhap.css").toExternalForm());
//        Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/adminView/TrangChu.fxml"));
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/adminView/TrangChu.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
//        primaryStage.setFullScreen(true);
    }
}
