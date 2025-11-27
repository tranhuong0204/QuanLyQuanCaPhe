package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.models.TaiKhoanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ThemTaiKhoanController {

    @FXML private TextField txtMaTK;
    @FXML private TextField txtTenTK;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cbChucVu;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private TaiKhoanController parent;

    public void setParent(TaiKhoanController parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {

        // Load chức vụ
        cbChucVu.getItems().addAll("Quản lý", "Nhân viên");

        // Tạo mã tài khoản tự động
        txtMaTK.setText(TaiKhoanDAO.generateNewId());
        txtMaTK.setDisable(true);

        btnLuu.setOnAction(e -> onSave());
        btnHuy.setOnAction(e -> close());
    }

    public void onSave() {
        String ma = txtMaTK.getText().trim();
        String ten = txtTenTK.getText().trim();
        String mk = txtMatKhau.getText().trim();
        String chucVu = cbChucVu.getValue();

        // Validate
        if (!validate(ma, ten, mk, chucVu)) return;

        TaiKhoan tk = new TaiKhoan(ma, ten, mk, chucVu);

        if (TaiKhoanDAO.insert(tk)) {
            show("Thêm tài khoản thành công!");
            if (parent != null) parent.loadData();
            close();
        } else {
            show("Thêm thất bại!");
        }
    }

    private boolean validate(String ma, String ten, String mk, String chucVu) {

        if (ten.isEmpty() || mk.isEmpty() || chucVu == null) {
            show("Không được để trống!");
            return false;
        }

        if (!ten.matches("^[a-zA-Z0-9._-]{4,20}$")) {
            show("Tên tài khoản 4-20 ký tự, không dấu, không khoảng trắng!");
            return false;
        }

        if (!mk.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            show("Mật khẩu phải có ít nhất 6 ký tự gồm chữ và số!");
            return false;
        }

        return true;
    }

    public void close() {
        Stage st = (Stage) btnHuy.getScene().getWindow();
        st.close();
    }

    private void show(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}
