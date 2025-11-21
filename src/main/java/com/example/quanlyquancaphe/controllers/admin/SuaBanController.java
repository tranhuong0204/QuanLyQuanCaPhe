package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SuaBanController {

    @FXML private TextField txtMaBan;
    @FXML private ComboBox<String> cbViTri;
    @FXML private TextField txtSoGhe;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private TextField txtGhiChu;
    @FXML private Button btnOK;

    private final BanDAO banDAO = new BanDAO();
    private QuanLyBanController parent;

    public void setParent(QuanLyBanController p) {
        this.parent = p;
    }

    /* ===============================
           COMBOBOX VỊ TRÍ
       =============================== */
    private void setupComboboxViTri() {
        cbViTri.getItems().addAll("Trong nhà", "Ngoài trời");

        cbViTri.setCellFactory(list -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                if (item.equals("Trong nhà")) setText("🏠 Trong nhà");
                else if (item.equals("Ngoài trời")) setText("🌳 Ngoài trời");
            }
        });

        // Button hiển thị giống item
        cbViTri.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }
                if (item.equals("Trong nhà")) setText("🏠 Trong nhà");
                else if (item.equals("Ngoài trời")) setText("🌳 Ngoài trời");
            }
        });
    }
    /* ===============================
           COMBOBOX TRẠNG THÁI
       =============================== */
    private void setupComboboxTrangThai() {
        cbTrangThai.getItems().addAll("Trống", "Có khách");

        cbTrangThai.setCellFactory(list -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                if (item.equals("Trống")) setStyle("🟢 Trống");
                else if (item.equals("Có khách")) setText("🔴 Có khách");
            }
        });

        cbTrangThai.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                if (item.equals("Trống")) setText("🟢 Trống");
                else if (item.equals("Có khách")) setText("🔴 Có khách");
            }
        });
    }


    /* ===============================
              KHỞI TẠO
       =============================== */
    public void initialize() {
        setupComboboxViTri();
        setupComboboxTrangThai();
    }


    /* ===============================
            SET DỮ LIỆU BAN ĐẦU
       =============================== */
    public void setData(Ban b) {
        txtMaBan.setText(b.getMaBan());
        txtMaBan.setDisable(true);

        cbViTri.setValue(b.getViTri());
        txtSoGhe.setText(String.valueOf(b.getSoGhe()));
        cbTrangThai.setValue(b.getTrangThai());
        txtGhiChu.setText(b.getGhiChu());
    }


    /* ===============================
              XỬ LÝ NÚT OK
       =============================== */
    @FXML
    private void onOK() {
        Ban b = new Ban(
                txtMaBan.getText(),
                cbViTri.getValue(),
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
