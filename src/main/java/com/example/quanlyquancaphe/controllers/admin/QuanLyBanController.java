package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.DAO.BanDAO;
import com.example.quanlyquancaphe.models.Ban;
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

public class QuanLyBanController {

    @FXML private TableView<Ban> tableBan;
    @FXML private TableColumn<Ban, String> colMaBan;
    @FXML private TableColumn<Ban, String> colViTri;
    @FXML private TableColumn<Ban, Integer> colSoGhe;
    @FXML private TableColumn<Ban, String> colTrangThai;
    @FXML private TableColumn<Ban, String> colGhiChu;
    @FXML private Button btnThem;
    @FXML private Button btnXoa;
    @FXML private Button btnSua;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private ComboBox<String> cbViTri;
    @FXML private TextField soGhe;
    @FXML private TextField noiDungTim;

    private BanDAO banDAO = new BanDAO();
    private ObservableList<Ban> listBan;

    @FXML
    public void initialize() {
        // --- TableView setup ---
        colMaBan.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaBan()));
        colViTri.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getViTri()));
        colSoGhe.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getSoGhe()).asObject());
        colTrangThai.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTrangThai()));
        colGhiChu.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGhiChu()));

        loadData();
        setupComboboxTrangThai();
        setupComboboxViTri();

        // --- Event xử lý ---
        btnThem.setOnAction(e -> onThem());
        btnSua.setOnAction(e -> onSua());
        btnXoa.setOnAction(e -> onXoa());

        // --- Tìm kiếm tự động ---
        noiDungTim.textProperty().addListener((obs, oldV, newV) -> onSearch());
        cbTrangThai.valueProperty().addListener((obs, oldV, newV) -> onSearch());
        cbViTri.valueProperty().addListener((obs, oldV, newV) -> onSearch());
        soGhe.textProperty().addListener((obs, oldV, newV) -> onSearch());

        tableBan.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Ban b = tableBan.getSelectionModel().getSelectedItem();
                if (b != null) {
                    onSua();
                }
            }
        });

    }

    // ==================== C O M B O B O X ==========================

    private void setupComboboxTrangThai() {
        cbTrangThai.getItems().addAll(null, "Trống", "Có khách");

        cbTrangThai.setConverter(new StringConverter<String>() {
            @Override public String toString(String value) {
                return value == null ? "— Chọn trạng thái —" : value;
            }
            @Override public String fromString(String string) { return null; }
        });

        cbTrangThai.setCellFactory(list -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                if (item == null) setText("— Chọn trạng thái —");
                else if (item.equals("Trống")) setText("🟢 Trống");
                else if (item.equals("Có khách")) setText("🔴 Có khách");
            }
        });

        cbTrangThai.setButtonCell(cbTrangThai.getCellFactory().call(null));
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

    // ==================== C R U D ==========================

    public void loadData() {
        listBan = FXCollections.observableArrayList(banDAO.getAll());
        tableBan.setItems(listBan);
    }

    @FXML
    private void onThem() {
        openNewWindow("/com/example/quanlyquancaphe/adminView/ThemBan.fxml", "Thêm bàn");
    }

    @FXML
    private void onSua() {
        Ban b = tableBan.getSelectionModel().getSelectedItem();
        if (b == null) { show("Vui lòng chọn bàn!"); return; }
        openNewWindowSuaBan("/com/example/quanlyquancaphe/adminView/SuaBan.fxml", "Sửa bàn", b);
    }

    @FXML
    private void onXoa() {
        Ban b = tableBan.getSelectionModel().getSelectedItem();
        if (b == null) { show("Chọn bàn để xóa"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn xóa bàn " + b.getMaBan() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Xác nhận xóa");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (banDAO.delete(b.getMaBan())) {
                loadData();
                show("Xóa thành công!");
            }
            else show("Xóa thất bại!");
        }
    }

    // ==================== TÌM KIẾM ==========================

    @FXML
    private void onSearch() {
        String noiDung = noiDungTim.getText().trim().toLowerCase();
        String trangThai = cbTrangThai.getValue();
        String viTri = cbViTri.getValue();
        String soGheStr = soGhe.getText().trim();

        ObservableList<Ban> filtered = FXCollections.observableArrayList();

        for (Ban b : listBan) {
            boolean matchNoiDung = b.getMaBan().toLowerCase().contains(noiDung)
                    || (b.getGhiChu() != null && b.getGhiChu().toLowerCase().contains(noiDung));

            boolean matchTrangThai = (trangThai == null) || b.getTrangThai().equals(trangThai);

            boolean matchViTri = (viTri == null) || b.getViTri().equals(viTri);

            boolean matchSoGhe = true;
            if (!soGheStr.isEmpty()) {
                try {
                    matchSoGhe = (b.getSoGhe() == Integer.parseInt(soGheStr));
                } catch (Exception e) { matchSoGhe = false; }
            }

            if (matchNoiDung && matchTrangThai && matchViTri && matchSoGhe) {
                filtered.add(b);
            }
        }
        tableBan.setItems(filtered);
    }

    // ==================== HỖ TRỢ ==========================

    private void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            // lấy controller và truyền parent
            Object controller = loader.getController();
            if (controller instanceof ThemBanController) {
                ((ThemBanController) controller).setParent(this);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openNewWindowSuaBan(String fxmlPath, String title, Ban selectedBan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            SuaBanController controller = loader.getController();
            controller.setParent(this);
            controller.setData(selectedBan);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void show(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }
}
