package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ThemBanController {

    @FXML private TextField txtMaBan;
    @FXML private ComboBox<String> cbViTri;
    @FXML private TextField txtSoGhe;
    @FXML private TextField txtGhiChu;
    @FXML private Button btnOK;

    private final BanDAO banDAO = new BanDAO();
    private QuanLyBanController parent;

    public void setParent(QuanLyBanController p) {
        this.parent = p;
    }
    private void setupComboboxViTri() {
        cbViTri.getItems().addAll(null, "Trong nhà", "Ngoài trời");

        cbViTri.setConverter(new StringConverter<String>() {
            @Override public String toString(String value) {
                return value == null ? "— Chọn vị trí —" : value;
            }
            @Override public String fromString(String string) { return null; }
        });

        cbViTri.setCellFactory(list -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                if (item == null) setText("— Chọn vị trí —");
                else if (item.equals("Trong nhà")) setText("🏠 Trong nhà");
                else if (item.equals("Ngoài trời")) setText("🌳 Ngoài trời");
            }
        });

        cbViTri.setButtonCell(cbViTri.getCellFactory().call(null));
    }
    @FXML
    public void initialize() {
        setupComboboxViTri();

    }

    @FXML
    private void onOK() {

        Ban b = new Ban(
                txtMaBan.getText(),
                cbViTri.getValue(),
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
