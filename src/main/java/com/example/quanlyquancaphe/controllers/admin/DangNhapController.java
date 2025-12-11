package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.services.DangNhapService;
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

public class DangNhapController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    // ĐÃ XÓA: private TaiKhoan taiKhoan;
    // ĐÃ XÓA: public void setTaiKhoan(TaiKhoan tk) { this.taiKhoan = tk; }

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) throws Exception{
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Vui lòng nhập đầy đủ thông tin.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        DangNhapService service = new DangNhapService();
        TaiKhoan tk = service.login(username, password);

        // Kiểm tra đối tượng tk có tồn tại không (đăng nhập thành công)
        if (tk != null) {
            statusLabel.setText("Đăng nhập thành công!");
            statusLabel.setStyle("-fx-text-fill: green;");

            // LƯU THÔNG TIN TÀI KHOẢN VÀO SESSION (đã sửa trong TaiKhoan.java)
            TaiKhoan.setUserLoggedIn(tk);

            // điều hướng theo role
            if ("quan ly".equalsIgnoreCase(tk.getChucVu())) {
                loadScene(event, "/com/example/quanlyquancaphe/adminView/TrangChu.fxml");
            } else if ("nhan vien".equalsIgnoreCase(tk.getChucVu())) {
                loadScene(event, "/com/example/quanlyquancaphe/employeeView/TrangChu.fxml");
            }
        } else {
            statusLabel.setText("Sai tên đăng nhập hoặc mật khẩu.");
        }
    }

}