package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SuaBanController {

    @FXML private TextField txtMaBan;
    @FXML private TextField txtViTri;
    @FXML private TextField txtSoGhe;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private TextField txtGhiChu;
    @FXML private Button btnOK;

    private final BanDAO banDAO = new BanDAO();
    private QuanLyBanController parent;

    public void setParent(QuanLyBanController p) {
        this.parent = p;
    }

    public void setData(Ban b) {
        txtMaBan.setText(b.getMaBan());
        txtMaBan.setDisable(true); // KHÔNG CHO SỬA

        txtViTri.setText(b.getViTri());
        txtSoGhe.setText(String.valueOf(b.getSoGhe()));
        cbTrangThai.setValue(b.getTrangThai());
        txtGhiChu.setText(b.getGhiChu());
    }

    @FXML
    private void onOK() {
        Ban b = new Ban(
                txtMaBan.getText(),
                txtViTri.getText(),
                Integer.parseInt(txtSoGhe.getText()),
                cbTrangThai.getValue(),
                txtGhiChu.getText()
        );

        if (banDAO.update(b)) {
            parent.loadData();
            close();
        } else {
            new Alert(Alert.AlertType.ERROR, "Cập nhật thất bại!").show();
        }
    }

    private void close() {
        Stage s = (Stage) btnOK.getScene().getWindow();
        s.close();
    }
}
