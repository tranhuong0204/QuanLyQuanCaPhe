package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.DAO.TaiKhoanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SuaTaiKhoanController {

    @FXML private TextField txtMaTK;
    @FXML private TextField txtTenTK;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cbChucVu;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private TaiKhoanController parent;
    private TaiKhoan taiKhoan; // Tài khoản gốc trước khi sửa

    public void setParent(TaiKhoanController parent) {
        this.parent = parent;
    }

    public void setData(TaiKhoan tk) {
        this.taiKhoan = tk;

        txtMaTK.setText(tk.getMaTaiKhoan());
        txtTenTK.setText(tk.getTenTaiKhoan());
        txtMatKhau.setText(tk.getMatKhau());
        cbChucVu.setValue(tk.getChucVu());

        txtMaTK.setDisable(true); // Không cho sửa khóa chính
    }

    @FXML
    public void initialize() {
        cbChucVu.getItems().addAll("Quản lý", "Nhân viên");

        btnLuu.setOnAction(e -> onSave());
        btnHuy.setOnAction(e -> close());
    }

    public void onSave() {
        String ma = txtMaTK.getText().trim();
        String ten = txtTenTK.getText().trim();
        String mk = txtMatKhau.getText().trim();
        String chucVu = cbChucVu.getValue();

        // Gọi validate tổng hợp trước khi lưu
        if (!validateInput(ten, mk, chucVu)) {
            return;
        }

        TaiKhoan tk = new TaiKhoan(
                taiKhoan.getMaTaiKhoan(), // Giữ nguyên Mã TK cũ
                ten,
                mk,
                chucVu
        );

        if (TaiKhoanDAO.update(tk)) {
            showSuccess("Cập nhật thành công!");
            if (parent != null) parent.loadData();
            close();
        } else {
            String errorMessage= "Cập nhật thất bại! (Có thể do lỗi DB)";
            showError(errorMessage,"Cập nhật thất bại");
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

        // 3. Kiểm tra trùng lặp Tên TK: Chỉ kiểm tra nếu tên mới khác tên cũ và đã tồn tại
        if (!ten.equalsIgnoreCase(taiKhoan.getTenTaiKhoan()) && TaiKhoanDAO.existsByUsername(ten)) {
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

    // Giữ lại phương thức show cũ để tránh lỗi nếu nó được gọi ở đâu đó ngoài onSave
    private void show(String msg) {
        showSuccess(msg);
    }
}