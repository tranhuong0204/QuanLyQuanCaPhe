package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.DAO.BanDAO;
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
        // 1. Kiểm tra không để trống
        String maBan = txtMaBan.getText().trim();
        String viTri = cbViTri.getValue();
        String soGheText = txtSoGhe.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();

        if (maBan.isEmpty() || viTri == null || soGheText.isEmpty() || ghiChu.isEmpty()) {
            showError("Không được để trống bất cứ trường nào!");
            return;
        }
        // 2. Kiểm tra số ghế phải là số
        int soGhe;
        try {
            soGhe = Integer.parseInt(soGheText);
            if (soGhe <= 0) {
                showError("Số ghế phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Số ghế phải là số hợp lệ!");
            return;
        }
        // 3. Kiểm tra mã bàn có bị trùng không
        if (banDAO.exists(maBan)) {
            showError("Mã bàn đã tồn tại! Vui lòng nhập mã khác.");
            return;
        }
        // ==============================
        // 4. Tạo đối tượng bàn mới
        // ==============================
        Ban b = new Ban(
                maBan,
                viTri,
                soGhe,
                "Trống",
                ghiChu
        );
        // ==============================
        // 5. Thêm vào DB
        // ==============================
        if (banDAO.insert(b)) {
            parent.loadData();
            close();
        } else {
            showError("Thêm bàn thất bại!");
        }
    }
    private void close() {
        Stage stage = (Stage) btnOK.getScene().getWindow();
        stage.close();
    }


    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

}
