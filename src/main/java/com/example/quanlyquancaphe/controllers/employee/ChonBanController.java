package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.models.Ban;
import com.example.quanlyquancaphe.DAO.BanDAO;
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

        cbTrangThai.valueProperty().addListener((obs, oldV, newV) -> onSearch());
        cbViTri.valueProperty().addListener((obs, oldV, newV) -> onSearch());


        tableListBan.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Ban b = tableListBan.getSelectionModel().getSelectedItem();
                if (b != null) {
                    // Kiểm tra trạng thái
                    if (b.getTrangThai().equals("Trống")) {
                        // 1. Cập nhật trạng thái trong DB thành "Có khách"
                        if (banDAO.updateTrangThai(b.getMaBan(), "Có khách")) {

                            // 2. Cập nhật đối tượng Ban trong bộ nhớ và giao diện
                            b.setTrangThai("Có khách");
                            tableListBan.refresh(); // Làm mới bảng

                            // 3. Mở cửa sổ Hóa Đơn và truyền đối tượng Ban đã cập nhật
                            openHoaDon("/com/example/quanlyquancaphe/employeeView/HoaDon.fxml", "Lập Hóa Đơn", b);
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Không thể chuyển trạng thái bàn trong DB.").show();
                        }
                    } else {
                        new Alert(Alert.AlertType.WARNING, "Bàn này hiện đang 'Có khách'.").show();
                    }
                }
            }
        });
    }

    // --- Load dữ liệu ---
    public void loadData() {
        listBan = FXCollections.observableArrayList(banDAO.getAll());
        tableListBan.setItems(listBan);
    }

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

    private void setupComboboxTrangThai() {
        cbTrangThai.getItems().addAll(null, "Trống", "Có khách");
        cbTrangThai.setConverter(new StringConverter<>() {
            @Override public String toString(String value) {
                return value == null ? "— Chọn trạng thái —" : value;
            }
            @Override public String fromString(String string) { return null; }
        });
    }

    private void setupComboboxViTri() {
        cbViTri.getItems().addAll(null, "Trong nhà", "Ngoài trời");
        cbViTri.setConverter(new StringConverter<>() {
            @Override public String toString(String value) {
                return value == null ? "— Chọn vị trí —" : value;
            }
            @Override public String fromString(String string) { return null; }
        });
    }


    private void openHoaDon(String fxmlPath, String title, Ban selectedBan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            HoaDonController controller = loader.getController();

            // TRUYỀN DỮ LIỆU BÀN VÀ THAM CHIẾU ĐẾN CHÍNH CONTROLLER NÀY
            controller.setData(selectedBan);
            controller.setParentController(this); // <<<< THÊM DÒNG NÀY

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
