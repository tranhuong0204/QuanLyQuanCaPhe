package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.HoaDon;
import com.example.quanlyquancaphe.DAO.HoaDonDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class QLHDController {

    @FXML private TableView<HoaDon> hoaDonTable;
    @FXML private TableColumn<HoaDon, String> colMaHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colTongKM;
    @FXML private TableColumn<HoaDon, java.math.BigDecimal> colTongTien; // sửa kiểu
    @FXML private TableColumn<HoaDon, java.util.Date> colNgayLap;
    @FXML private TableColumn<HoaDon, String> colMaBan;
    @FXML private TableColumn<HoaDon, String> colMaTaiKhoan;
    @FXML private TableColumn<HoaDon, String> colMaPT;
    @FXML private TableColumn<HoaDon, Void> colAction;
    @FXML private TextField searchField;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ObservableList<HoaDon> hoaDonList;

    @FXML
    public void initialize() {
        colMaHoaDon.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        colTongKM.setCellValueFactory(new PropertyValueFactory<>("tongKM"));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colNgayLap.setCellValueFactory(new PropertyValueFactory<>("ngayLap"));
        colMaBan.setCellValueFactory(new PropertyValueFactory<>("maBan"));
        colMaTaiKhoan.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoan"));
        colMaPT.setCellValueFactory(new PropertyValueFactory<>("maPT"));

        addDeleteButtonToTable();
        loadHoaDon();
    }

    private void loadHoaDon() {
        List<HoaDon> list = hoaDonDAO.getAllHoaDon();
        System.out.println("Số hóa đơn lấy được: " + list.size()); // log kiểm tra
        hoaDonList = FXCollections.observableArrayList(list);
        hoaDonTable.setItems(hoaDonList);
    }

    private void addDeleteButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setOnAction(event -> {
                    HoaDon hd = getTableView().getItems().get(getIndex());
                    boolean ok = hoaDonDAO.deleteHoaDon(hd.getMaHoaDon());
                    if (ok) {
                        hoaDonList.remove(hd);
                        new Alert(Alert.AlertType.INFORMATION, "Xóa thành công!").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Xóa thất bại!").showAndWait();
                    }
                });
                btn.getStyleClass().add("button-delete");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        List<HoaDon> filtered = hoaDonList.stream()
                .filter(hd -> hd.getMaHoaDon().toLowerCase().contains(keyword)
                        || hd.getNgayLap().toString().contains(keyword))
                .collect(Collectors.toList());
        hoaDonTable.setItems(FXCollections.observableArrayList(filtered));
    }
}
