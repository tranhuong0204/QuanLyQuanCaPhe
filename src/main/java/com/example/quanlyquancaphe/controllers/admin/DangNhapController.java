package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DangNhapController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;


    @FXML
    private void handleLogin(ActionEvent event) throws Exception{
        String username = usernameField.getText();
        String password = passwordField.getText();
        //String role =

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Vui lòng nhập đầy đủ thông tin.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM TAIKHOAN WHERE tenTaiKhoan = ? AND matKhau = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("chucVu");
                statusLabel.setText("Đăng nhập thành công!");
                statusLabel.setStyle("-fx-text-fill: green;");
                // chuyển sang màn hình chính
                if("quan ly".equalsIgnoreCase(role)) {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/adminView/TrangChu.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //mark
                    Scene scene = new Scene(root);
//                scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/TrangChu.css").toExternalForm());
                    stage.setScene(scene);
                    stage.show();
                    stage.centerOnScreen();
                    stage.setMaximized(true); // full màn hình
                } else if ("nhan vien".equalsIgnoreCase(role)) {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/example/quanlyquancaphe/employeeView/TrangChu.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //mark
                    Scene scene = new Scene(root);
//                scene.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/TrangChu.css").toExternalForm());
                    stage.setScene(scene);
                    stage.show();
                    stage.centerOnScreen();
                    stage.setMaximized(true); // full màn hình

                }
                // chuyển sang màn hình chính nếu cần
            } else {
                statusLabel.setText("Sai tên đăng nhập hoặc mật khẩu.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Lỗi kết nối CSDL.");
            e.printStackTrace();
        }
    }

}
