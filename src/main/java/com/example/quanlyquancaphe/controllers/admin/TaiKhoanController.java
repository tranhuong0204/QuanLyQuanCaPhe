package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.models.TaiKhoanDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class TaiKhoanController {

    @FXML private TableView<TaiKhoan> tableTaiKhoan;
    @FXML private TableColumn<TaiKhoan, String> colMaTaiKhoan;
    @FXML private TableColumn<TaiKhoan, String> colTenTaiKhoan;
    @FXML private TableColumn<TaiKhoan, String> colMatKhau;
    @FXML private TableColumn<TaiKhoan, String> colChucVu;

    @FXML private TextField noiDungTim;
    @FXML private ComboBox<String> cbChucVu;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private ObservableList<TaiKhoan> listTK;

    @FXML
    public void initialize() {

        // ==================== TABLE MAPPING ====================
        colMaTaiKhoan.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getMaTaiKhoan()));

        colTenTaiKhoan.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTenTaiKhoan()));

        colMatKhau.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getMatKhau()));

        colChucVu.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getChucVu()));

        loadData();
        setupComboboxChucVu();

        // ==================== EVENTS ====================
        btnThem.setOnAction(e -> onThem());
        btnSua.setOnAction(e -> onSua());
        btnXoa.setOnAction(e -> onXoa());

        noiDungTim.textProperty().addListener((o, oldVal, newVal) -> onSearch());
        cbChucVu.valueProperty().addListener((o, oldVal, newVal) -> onSearch());

        tableTaiKhoan.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) onSua();
        });
    }

    // ==================== COMBOBOX ====================
    private void setupComboboxChucVu() {
        cbChucVu.getItems().addAll("Tất cả", "Quản lý", "Nhân viên");
        cbChucVu.setValue("Tất cả");
    }

    // ==================== LOAD DATA ====================
    public void loadData() {
        listTK = FXCollections.observableArrayList(TaiKhoanDAO.getAll());
        tableTaiKhoan.setItems(listTK);
    }

    // ==================== CRUD ====================
    public void onThem() {
        openWindow("/com/example/quanlyquancaphe/adminView/ThemTaiKhoan.fxml",
                "Thêm tài khoản");
    }

    public void onSua() {
        TaiKhoan tk = tableTaiKhoan.getSelectionModel().getSelectedItem();
        if (tk == null) {
            show("Bạn chưa chọn tài khoản!");
            return;
        }

        openWindowSua("/com/example/quanlyquancaphe/adminView/SuaTaiKhoan.fxml",
                "Sửa tài khoản", tk);
    }

    public void onXoa() {
        TaiKhoan tk = tableTaiKhoan.getSelectionModel().getSelectedItem();
        if (tk == null) {
            show("Bạn chưa chọn tài khoản để xoá!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn muốn xoá tài khoản \"" + tk.getTenTaiKhoan() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (TaiKhoanDAO.delete(tk.getMaTaiKhoan())) {
                loadData();
            } else {
                show("Xoá thất bại!");
            }
        }
    }

    // ==================== SEARCH ====================
    private void onSearch() {
        String keyword = noiDungTim.getText().trim().toLowerCase();
        String role = cbChucVu.getValue();

        ObservableList<TaiKhoan> filtered = FXCollections.observableArrayList();

        for (TaiKhoan tk : listTK) {

            boolean matchKeyword =
                    tk.getMaTaiKhoan().toLowerCase().contains(keyword)
                            || tk.getTenTaiKhoan().toLowerCase().contains(keyword);

            boolean matchRole =
                    role.equals("Tất cả") || tk.getChucVu().equalsIgnoreCase(role);

            if (matchKeyword && matchRole) {
                filtered.add(tk);
            }
        }

        tableTaiKhoan.setItems(filtered);
    }

    // ==================== OPEN WINDOWS ====================

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Object ctr = loader.getController();
            if (ctr instanceof ThemTaiKhoanController)
                ((ThemTaiKhoanController) ctr).setParent(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openWindowSua(String fxml, String title, TaiKhoan tk) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            SuaTaiKhoanController ctr = loader.getController();
            ctr.setParent(this);
            ctr.setData(tk);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== ALERT ====================
    private void show(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }
}
