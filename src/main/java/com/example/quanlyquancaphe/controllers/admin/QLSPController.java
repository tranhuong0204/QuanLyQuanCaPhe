package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.SanPham;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QLSPController {

    // Các trường nhập liệu
    @FXML
    private TextField txtMaSanPham;
    @FXML
    private TextField txtTenSanPham;
    @FXML
    private TextField txtDonGia;
    @FXML
    private TextArea txtMoTa;
    @FXML
    private ImageView imgSanPham;

    // Các nút thao tác
    @FXML
    private Button btnChonAnh;
    @FXML
    private Button btnThem;
    @FXML
    private Button btnTim;
    @FXML
    private TextField txtTimKiem;

    // Bảng dữ liệu
    @FXML
    private TableView<SanPham> tableSanPham;
    @FXML
    private TableColumn<SanPham, String> colSTT;
    @FXML
    private TableColumn<SanPham, String> colMaSanPham;
    @FXML
    private TableColumn<SanPham, String> colTenSanPham;
    @FXML
    private TableColumn<SanPham, String> colAnh;
    @FXML
    private TableColumn<SanPham, Double> colDonGia;
    @FXML
    private TableColumn<SanPham, String> colMoTa;
    @FXML
    private TableColumn<SanPham, String> colThaoTac;

    private ObservableList<SanPham> danhSach = FXCollections.observableArrayList();
    private String selectedImagePath = null;

    @FXML
    public void initialize() {
        // Các cột text
        colMaSanPham.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMa()));
        colTenSanPham.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTen()));
        colDonGia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDonGia()));
        colMoTa.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMoTa()));

        // Cột ảnh: hiển thị thumbnail
        colAnh.setCellFactory(column -> new TableCell<SanPham, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(60);   // chiều cao thumbnail
                imageView.setFitWidth(60);    // chiều rộng thumbnail
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    Image image = new Image("file:" + imagePath, 60, 60, true, true);
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });
        colAnh.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHinhAnh()));

        tableSanPham.setItems(danhSach);

        // Load dữ liệu từ DB
        loadDataFromDatabase();

        // Cột thao tác: hiển thị nút Sửa và Xóa
        colThaoTac.setCellFactory(column -> new TableCell<SanPham, String>() {
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox hbox = new HBox(5, btnSua, btnXoa);

            {
                // Xử lý nút Sửa
                btnSua.setOnAction(event -> {
                    SanPham sp = getTableView().getItems().get(getIndex());
                    if (sp != null) {
                        // Đưa dữ liệu lên form để sửa
                        txtMaSanPham.setText(sp.getMa());
                        txtTenSanPham.setText(sp.getTen());
                        txtDonGia.setText(String.valueOf(sp.getDonGia()));
                        txtMoTa.setText(sp.getMoTa());
                        if (sp.getHinhAnh() != null) {
                            imgSanPham.setImage(new Image("file:" + sp.getHinhAnh()));
                        }
                    }
                });

                // Xử lý nút Xóa
                btnXoa.setOnAction(event -> {
                    SanPham sp = getTableView().getItems().get(getIndex());
                    if (sp != null) {
                        // Xóa trong DB
                        String sql = "DELETE FROM MON WHERE maMon=?";
                        try (Connection conn = DatabaseConnection.getConnection();
                             PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, sp.getMa());
                            ps.executeUpdate();

                            // Xóa trong danh sách
                            danhSach.remove(sp);
                            showAlert("Thành công", "Đã xóa sản phẩm!", Alert.AlertType.INFORMATION);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert("Lỗi", "Không thể xóa sản phẩm!", Alert.AlertType.ERROR);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

    }

    private void loadDataFromDatabase() {
        danhSach.clear();
        String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM MON";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("maMon");
                String ten = rs.getString("tenMon");
                double donGia = rs.getDouble("giaCa");
                String moTa = rs.getString("moTa");
                String hinhAnh = rs.getString("hinhAnh");

                danhSach.add(new SanPham(ma, ten, donGia, moTa, hinhAnh));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể load dữ liệu từ SQL Server!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onThem() {
        String ma = txtMaSanPham.getText().trim();
        String ten = txtTenSanPham.getText().trim();
        String moTa = txtMoTa.getText().trim();
        double donGia = Double.parseDouble(txtDonGia.getText().trim());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra sản phẩm đã tồn tại chưa
            String checkSql = "SELECT COUNT(*) FROM MON WHERE maMon=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, ma);
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // Nếu tồn tại → UPDATE
                String updateSql = "UPDATE MON SET tenMon=?, giaCa=?, moTa=?, hinhAnh=? WHERE maMon=?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, ten);
                ps.setDouble(2, donGia);
                ps.setString(3, moTa);
                ps.setString(4, selectedImagePath);
                ps.setString(5, ma);
                ps.executeUpdate();

                showAlert("Thành công", "Đã cập nhật sản phẩm!", Alert.AlertType.INFORMATION);
            } else {
                // Nếu chưa có → INSERT
                String insertSql = "INSERT INTO MON(maMon, tenMon, giaCa, moTa, hinhAnh) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setString(1, ma);
                ps.setString(2, ten);
                ps.setDouble(3, donGia);
                ps.setString(4, moTa);
                ps.setString(5, selectedImagePath);
                ps.executeUpdate();

                showAlert("Thành công", "Đã thêm sản phẩm mới!", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể lưu sản phẩm!", Alert.AlertType.ERROR);
        }

        loadDataFromDatabase(); // refresh lại bảng
    }



    @FXML
    private void onSua() {
        SanPham sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) {
            showAlert("Thông báo", "Vui lòng chọn sản phẩm cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        String ma = txtMaSanPham.getText();
        String ten = txtTenSanPham.getText();
        String moTa = txtMoTa.getText();
        double donGia;

        try {
            donGia = Double.parseDouble(txtDonGia.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Đơn giá phải là số hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        String sql = "UPDATE MON SET tenMon=?, giaCa=?, moTa=?, hinhAnh=? WHERE maMon=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ten);
            ps.setDouble(2, donGia);
            ps.setString(3, moTa);
            ps.setString(4, selectedImagePath);
            ps.setString(5, ma);
            ps.executeUpdate();

            sp.setTen(ten);
            sp.setDonGia(donGia);
            sp.setMoTa(moTa);
            sp.setHinhAnh(selectedImagePath);
            tableSanPham.refresh();

            showAlert("Thành công", "Đã cập nhật sản phẩm!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể sửa sản phẩm!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onXoa() {
        SanPham sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) {
            showAlert("Thông báo", "Vui lòng chọn sản phẩm cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        String sql = "DELETE FROM MON WHERE maMon=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sp.getMa());
            ps.executeUpdate();

            danhSach.remove(sp);
            showAlert("Thành công", "Đã xóa sản phẩm!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xóa sản phẩm!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onTim() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            tableSanPham.setItems(danhSach);
            return;
        }

        ObservableList<SanPham> ketQua = FXCollections.observableArrayList();
        for (SanPham sp : danhSach) {
            if (sp.getMa().toLowerCase().contains(keyword) || sp.getTen().toLowerCase().contains(keyword)) {
                ketQua.add(sp);
            }
        }
        tableSanPham.setItems(ketQua);
    }

    @FXML
    private void onChonDong() {
        SanPham sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp != null) {
            txtMaSanPham.setText(sp.getMa());
            txtTenSanPham.setText(sp.getTen());
            txtDonGia.setText(String.valueOf(sp.getDonGia()));
            txtMoTa.setText(sp.getMoTa());
            if (sp.getHinhAnh() != null) {
                imgSanPham.setImage(new Image("file:" + sp.getHinhAnh()));
            }
        }
    }

    @FXML
    private void onChonAnh() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            imgSanPham.setImage(new Image(file.toURI().toString()));
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
    }
}
