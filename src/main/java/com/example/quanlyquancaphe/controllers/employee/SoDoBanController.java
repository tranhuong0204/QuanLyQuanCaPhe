package com.example.quanlyquancaphe.controllers.employee;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SoDoBanController implements Initializable {

    @FXML
    private Pane trongNhaPane; // Pane chứa tất cả bàn + quầy thu ngân

    // Map lưu trạng thái bàn theo Rectangle
    // true = có khách, false = trống
    private Map<Rectangle, Boolean> tableStatus = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Duyệt toàn bộ Node trong Pane và tìm Rectangle có styleClass "ban"
        for (Node node : trongNhaPane.getChildren()) {

            if (node instanceof Rectangle rect && rect.getStyleClass().contains("ban")) {

                // Đặt trạng thái mặc định là trống
                tableStatus.put(rect, false);

                // Tô màu xanh lá = bàn trống
                rect.setFill(Color.web("#4CAF50"));

                // Gán sự kiện click
                rect.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleTableClick(rect));
            }
        }
    }

    private void handleTableClick(Rectangle rect) {

        boolean isBusy = tableStatus.get(rect);

        if (isBusy) {
            // Bàn đang có khách
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Bàn đã có khách!");
            alert.show();
        } else {
            // Bàn trống → mở cửa sổ thêm món
            openAddDishWindow(rect);
        }
    }

    private void openAddDishWindow(Rectangle rect) {
        // TODO: mở form thêm món

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thêm món");
        alert.setHeaderText(null);
        alert.setContentText("Mở danh sách món cho bàn này...");
        alert.show();

        // Sau khi thêm món đầu tiên, đánh dấu bàn là có khách
        rect.setFill(Color.web("#E53935"));
        tableStatus.put(rect, true);
    }
}
