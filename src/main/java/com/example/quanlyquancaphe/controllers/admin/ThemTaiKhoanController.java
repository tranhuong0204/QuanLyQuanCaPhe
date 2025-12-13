package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.DAO.TaiKhoanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ThemTaiKhoanController {

    @FXML private TextField txtMaTK;
    @FXML private TextField txtTenTK;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cbChucVu;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private TaiKhoanController parent;
    private String generatedId; // Lưu ID đã sinh ra khi initialize

    public void setParent(TaiKhoanController parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {

        // Load chức vụ
        cbChucVu.getItems().addAll("Quản lý", "Nhân viên");

        // 1. TẠO MÃ TÀI KHOẢN TỰ ĐỘNG VÀ LƯU VÀO BIẾN generatedId
        generatedId = TaiKhoanDAO.generateNewId();
        txtMaTK.setText(generatedId);
        txtMaTK.setDisable(true); // Vô hiệu hóa trường Mã TK

        btnLuu.setOnAction(e -> onSave());
        btnHuy.setOnAction(e -> close());
    }

    public void onSave() {
        // 2. SỬ DỤNG ID ĐÃ SINH LÚC INITIALIZE
        String ma = generatedId;
        String ten = txtTenTK.getText().trim();
        String mk = txtMatKhau.getText().trim();
        String chucVu = cbChucVu.getValue();

        // Validate tổng hợp
        if (!validateInput(ten, mk, chucVu)) return;

        TaiKhoan tk = new TaiKhoan(ma, ten, mk, chucVu);

        if (TaiKhoanDAO.insert(tk)) {
            showSuccess("Thêm tài khoản thành công!");
            if (parent != null) parent.loadData();
            close();
        } else {
            String errorMessage = "Thêm thất bại! (Có thể do lỗi DB)";
            showError(errorMessage,"Thêm thất bại!");
        }
    }


    private boolean validateInput(String ten, String mk, String chucVu) {

        List<String> errors = new ArrayList<>();

        // 1. Kiểm tra trống
        if (ten.isEmpty()) {
            errors.add("- Tên tài khoản không được để trống.");
        }
        if (mk.isEmpty()) {
            errors.add("- Mật khẩu không được để trống.");
        }
        if (chucVu == null || chucVu.isEmpty()) {
            errors.add("- Vui lòng chọn chức vụ.");
        }

        // 2. Kiểm tra định dạng Tên TK (Chỉ kiểm tra nếu Tên TK không trống)
        if (!ten.isEmpty() && !ten.matches("^[a-zA-Z0-9._-]{4,20}$")) {
            errors.add("- Tên tài khoản 4-20 ký tự, không dấu, không khoảng trắng.");
        }

        // 3. Kiểm tra trùng lặp Tên TK (Chỉ kiểm tra nếu Tên TK hợp lệ về định dạng)
        if (errors.isEmpty() && TaiKhoanDAO.existsByUsername(ten)) {
            errors.add("- Tên tài khoản đã tồn tại trong hệ thống.");
        }

        // 4. Kiểm tra định dạng Mật khẩu (Chỉ kiểm tra nếu Mật khẩu không trống)
        if (!mk.isEmpty() && !mk.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            errors.add("- Mật khẩu phải có ít nhất 6 ký tự gồm chữ và số.");
        }

        // ==========================================================
        // Xử lý và hiển thị lỗi
        // ==========================================================

        if (errors.isEmpty()) {
            return true;
        } else {
            String errorMessage = "Vui lòng khắc phục các lỗi sau:\n\n" +
                    String.join("\n", errors);

            showError(errorMessage, "Lỗi Nhập Liệu");
            return false;
        }
    }

    public void close() {
        Stage st = (Stage) btnHuy.getScene().getWindow();
        st.close();
    }

    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    private void showError(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Phương thức show cũ được thay bằng showSuccess và showError
    private void show(String msg) {
        showSuccess(msg);
    }
}