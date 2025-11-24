package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.SanPham;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

@SuppressWarnings({"unused"})
public class ProductSelectionController {
    @FXML private TableView<SanPham> tableSanPham;
    @FXML private TableColumn<SanPham, Boolean> colChon;
    @FXML private TableColumn<SanPham, String> colMa;
    @FXML private TableColumn<SanPham, String> colTen;

    private final ObservableList<SanPham> danhSach = FXCollections.observableArrayList();
    private final ObservableList<SanPham> daChon = FXCollections.observableArrayList();
    private Consumer<ObservableList<SanPham>> callback;

    public void setCallback(Consumer<ObservableList<SanPham>> callback) {
        this.callback = callback;
    }

    @FXML
    private void initialize() {
        // Bind columns
        colMa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMa()));
        colTen.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTen()));
        colChon.setCellValueFactory(data -> new SimpleBooleanProperty(daChon.contains(data.getValue())));

        colChon.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(_ -> {
                    SanPham sp = getTableView().getItems().get(getIndex());
                    if (checkBox.isSelected()) {
                        if (!daChon.contains(sp)) daChon.add(sp);
                    } else {
                        daChon.remove(sp);
                    }
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    SanPham sp = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(daChon.contains(sp));
                    setGraphic(checkBox);
                }
            }
        });

        tableSanPham.setItems(danhSach);
        loadProducts();
    }

    private void loadProducts() {
        danhSach.clear();
        String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM MON";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("maMon");
                String ten = rs.getString("tenMon");
                double gia = rs.getDouble("giaCa");
                String moTa = rs.getString("moTa");
                String hinhAnh = rs.getString("hinhAnh");
                danhSach.add(new SanPham(ma, ten, gia, moTa, hinhAnh));
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onApDung() {
        if (callback != null) {
            callback.accept(FXCollections.observableArrayList(daChon));
        }
        close();
    }

    @FXML
    private void onHuy() {
        close();
    }

    private void close() {
        Stage stage = (Stage) tableSanPham.getScene().getWindow();
        stage.close();
    }

    private void showError(String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Không thể tải danh sách sản phẩm");
        alert.setContentText(details);
        alert.showAndWait();
    }
}
