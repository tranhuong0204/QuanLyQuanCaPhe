package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.Ban;
import com.example.quanlyquancaphe.models.BanDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;

public class ChonBanController {

    @FXML private TableView<Ban> tableListBan;
    @FXML private TableColumn<Ban, String> colMaBan;
    @FXML private TableColumn<Ban, String> colViTri;
    @FXML private TableColumn<Ban, String> colTrangThai;
    @FXML private TableColumn<Ban, Integer> colSoGhe;
    @FXML private TableColumn<Ban, String> colGhiChu;

    @FXML private ComboBox<String> cbTrangThai;
    @FXML private ComboBox<String> cbViTri;

    private BanDAO banDAO = new BanDAO();
    private ObservableList<Ban> listBan;

    @FXML
    public void initialize() {
        // --- Setup TableView ---
        colMaBan.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaBan()));
        colViTri.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getViTri()));
        colSoGhe.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getSoGhe()).asObject());
        colTrangThai.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTrangThai()));
        colGhiChu.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGhiChu()));

        loadData();
        setupComboboxTrangThai();
        setupComboboxViTri();

        // --- Filter tự động ---
        cbTrangThai.valueProperty().addListener((obs, oldV, newV) -> onSearch());
        cbViTri.valueProperty().addListener((obs, oldV, newV) -> onSearch());

        // --- Double click mở ChonMon ---
        tableListBan.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Ban b = tableListBan.getSelectionModel().getSelectedItem();
                if (b != null) {
                    openChonMon("/com/example/quanlyquancaphe/employeeView/ChonMon.fxml","Chọn món",b);
                }
            }
        });
    }

    // --- Load dữ liệu ---
    public void loadData() {
        listBan = FXCollections.observableArrayList(banDAO.getAll());
        tableListBan.setItems(listBan);
    }

    // --- Filter ---
    @FXML
    private void onSearch() {
        String trangThai = cbTrangThai.getValue();
        String viTri = cbViTri.getValue();
        ObservableList<Ban> filtered = FXCollections.observableArrayList();

        for (Ban b : listBan) {
            boolean matchTrangThai = (trangThai == null) || b.getTrangThai().equals(trangThai);
            boolean matchViTri = (viTri == null) || b.getViTri().equals(viTri);
            if (matchTrangThai && matchViTri) filtered.add(b);
        }

        tableListBan.setItems(filtered);
    }

    // --- ComboBox Trạng thái ---
    private void setupComboboxTrangThai() {
        cbTrangThai.getItems().addAll(null, "Trống", "Có khách");
        cbTrangThai.setConverter(new StringConverter<>() {
            @Override public String toString(String value) {
                return value == null ? "— Chọn trạng thái —" : value;
            }
            @Override public String fromString(String string) { return null; }
        });
    }

    // --- ComboBox Vị trí ---
    private void setupComboboxViTri() {
        cbViTri.getItems().addAll(null, "Trong nhà", "Ngoài trời");
        cbViTri.setConverter(new StringConverter<>() {
            @Override public String toString(String value) {
                return value == null ? "— Chọn vị trí —" : value;
            }
            @Override public String fromString(String string) { return null; }
        });
    }

    // --- Mở ChonMon.fxml ---
    private void openChonMon(String fxmlPath, String title, Ban selectedBan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Cast đúng controller của ChonMon.fxml
            ChonMonController controller = loader.getController();
            controller.setData(selectedBan);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
