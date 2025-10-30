package com.example.quanlyquancaphe;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM TAIKHOAN WHERE tenTaiKhoan = ? AND matKhau = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                statusLabel.setText("Đăng nhập thành công!");
                statusLabel.setStyle("-fx-text-fill: green;");
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
