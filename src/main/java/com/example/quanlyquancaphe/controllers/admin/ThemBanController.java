package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ThemBanController {

    @FXML private TextField txtMaBan;
    @FXML private TextField txtViTri;
    @FXML private TextField txtSoGhe;
    @FXML private TextField txtGhiChu;
    @FXML private Button btnOK;

    private final BanDAO banDAO = new BanDAO();
    private QuanLyBanController parent;

    public void setParent(QuanLyBanController p) {
        this.parent = p;
    }

    @FXML
    private void onOK() {

        Ban b = new Ban(
                txtMaBan.getText(),
                txtViTri.getText(),
                Integer.parseInt(txtSoGhe.getText()),
                "Trống",        // mặc định
                txtGhiChu.getText()
        );

        if (banDAO.insert(b)) {
            parent.loadData();
            close();
        } else {
            new Alert(Alert.AlertType.ERROR, "Thêm bàn thất bại!").show();
        }
    }

    private void close() {
        Stage s = (Stage) btnOK.getScene().getWindow();
        s.close();
    }
}
