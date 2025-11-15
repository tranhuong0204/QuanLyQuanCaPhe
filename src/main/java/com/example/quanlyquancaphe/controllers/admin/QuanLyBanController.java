package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class QuanLyBanController {

    @FXML private TableView<Ban> tableBan;
    @FXML private TableColumn<Ban, String> colMaBan;
    @FXML private TableColumn<Ban, String> colViTri;
    @FXML private TableColumn<Ban, Integer> colSoGhe;
    @FXML private TableColumn<Ban, String> colTrangThai;
    @FXML private TableColumn<Ban, String> colGhiChu;

    private BanDAO banDAO = new BanDAO();
    private ObservableList<Ban> listBan;

    @FXML
    public void initialize() {
        colMaBan.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaBan()));
        colViTri.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getViTri()));
        colSoGhe.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getSoGhe()).asObject());
        colTrangThai.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTrangThai()));
        colGhiChu.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGhiChu()));

        loadData();
    }

    public void loadData() {
        listBan = FXCollections.observableArrayList(banDAO.getAll());
        tableBan.setItems(listBan);
    }

    @FXML
    private void onThem() {
        openForm("/com/example/quanlyquancaphe/ThemBan.fxml", false);
    }

    @FXML
    private void onSua() {
        Ban b = tableBan.getSelectionModel().getSelectedItem();
        if (b == null) {
            show("Vui lòng chọn bàn!");
            return;
        }
        openForm("/com/example/quanlyquancaphe/SuaBan.fxml", true);
    }

    @FXML
    private void onXoa() {
        Ban b = tableBan.getSelectionModel().getSelectedItem();
        if (b == null) {
            show("Chọn bàn để xóa");
            return;
        }
        if (banDAO.delete(b.getMaBan())) {
            loadData();
        }
    }

    private void openForm(String fxml, boolean isEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            SuaBanController controller = loader.getController();
            controller.setParent(this);

            if (isEdit) {
                controller.setData(tableBan.getSelectionModel().getSelectedItem());
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }
}
