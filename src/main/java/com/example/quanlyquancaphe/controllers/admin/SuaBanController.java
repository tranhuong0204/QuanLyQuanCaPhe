package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.DAO.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
                if (item.equals("Trống")) setText("🟢 Trống"); // Đã sửa setStyle thành setText
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
        String maBan = txtMaBan.getText();
        String viTri = cbViTri.getValue();
        String soGheText = txtSoGhe.getText().trim();
        String trangThai = cbTrangThai.getValue();
        String ghiChu = txtGhiChu.getText();

        // 1. Validate
        if (!validateInput(soGheText, viTri, trangThai)) {
            return;
        }

        // 2. Chuyển đổi dữ liệu sau khi đã validate
        int soGhe = Integer.parseInt(soGheText);

        Ban b = new Ban(maBan, viTri, soGhe, trangThai, ghiChu);

        if (banDAO.update(b)) {
            new Alert(Alert.AlertType.INFORMATION, "Cập nhật bàn thành công!").show();
            if (parent != null) parent.loadData();
            close();
        } else {
            showError("Cập nhật thất bại!", "Lỗi DB");
        }
    }

    /* ===============================
              VALIDATION TỔNG HỢP
       =============================== */
    private boolean validateInput(String soGheText, String viTri, String trangThai) {
        List<String> errors = new ArrayList<>();

        // 1. Kiểm tra trống/Chọn
        if (soGheText.isEmpty()) {
            errors.add("- Số ghế không được để trống.");
        }
        if (viTri == null || viTri.isEmpty()) {
            errors.add("- Vui lòng chọn vị trí cho bàn.");
        }
        if (trangThai == null || trangThai.isEmpty()) {
            errors.add("- Vui lòng chọn trạng thái cho bàn.");
        }

        // 2. Kiểm tra định dạng Số Ghế (Phải là số nguyên dương)
        if (!soGheText.isEmpty()) {
            try {
                int soGhe = Integer.parseInt(soGheText);
                if (soGhe <= 0) {
                    errors.add("- Số ghế phải là số nguyên dương (> 0).");
                }
            } catch (NumberFormatException e) {
                errors.add("- Số ghế phải là số nguyên (không chứa chữ cái hay ký tự đặc biệt).");
            }
        }

        // 3. Kiểm tra các lỗi khác (Nếu cần, ví dụ: độ dài Ghi Chú)
        // ... (Bạn có thể thêm các kiểm tra khác ở đây)

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

    private void close() {
        Stage s = (Stage) btnOK.getScene().getWindow();
        s.close();
    }

    private void showError(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Giữ lại hàm showError cũ để tránh lỗi gọi hàm bị thiếu tham số
    private void showError(String msg) {
        showError(msg, "Cảnh báo");
    }
}