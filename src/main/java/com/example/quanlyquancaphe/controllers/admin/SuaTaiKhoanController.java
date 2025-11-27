package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.models.TaiKhoanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SuaTaiKhoanController {

    @FXML private TextField txtMaTK;
    @FXML private TextField txtTenTK;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cbChucVu;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private TaiKhoanController parent;
    private TaiKhoan taiKhoan;

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

        // Gọi validate trước khi lưu
        if (!validate(ma, ten, mk, chucVu)) {
            return;
        }

        TaiKhoan tk = new TaiKhoan(
                taiKhoan.getMaTaiKhoan(),
                ten,
                mk,
                chucVu
        );

        if (TaiKhoanDAO.update(tk)) {
            show("Cập nhật thành công!");
            parent.loadData();
            close();
        } else {
            show("Cập nhật thất bại!");
        }
    }


    public void close() {
        Stage st = (Stage) btnHuy.getScene().getWindow();
        st.close();
    }

    private void show(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
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


}
