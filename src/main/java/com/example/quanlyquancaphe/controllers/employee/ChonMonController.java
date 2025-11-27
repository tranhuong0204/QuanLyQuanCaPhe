package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.Ban;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ChonMonController {

    private Ban selectedBan;

    @FXML private Label lblBan; // ví dụ hiển thị thông tin bàn

    // Nhận dữ liệu từ ChonBanController
    public void setData(Ban ban) {
        this.selectedBan = ban;

        if (lblBan != null && ban != null) {
            lblBan.setText("Bàn: " + ban.getMaBan() + " (" + ban.getViTri() + ")");
        }
    }

    @FXML
    public void initialize() {
        // Khởi tạo UI nếu cần
    }
}
