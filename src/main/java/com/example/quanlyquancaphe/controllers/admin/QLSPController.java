package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.SanPham;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings({"unused"})
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
    @FXML private Button btnChonAnh; // accessed via FXML
    @FXML private Button btnThem;    // accessed via FXML
    @FXML private Button btnTim;     // accessed via FXML
    @FXML private TextField txtTimKiem; // search field (restored)

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

    private final ObservableList<SanPham> danhSach = FXCollections.observableArrayList(); // made final
    private String selectedImagePath = null; // will store resource-relative path like /com/example/quanlyquancaphe/images/foo.jpg
    private static final String RESOURCE_IMAGE_DIR = "src/main/resources/com/example/quanlyquancaphe/images"; // project resource folder
    private static final String RESOURCE_IMAGE_PREFIX = "/com/example/quanlyquancaphe/images/";

    @FXML
    public void initialize() {
        // touch buttons to mark usage
        if (btnChonAnh != null) btnChonAnh.getStyle();
        if (btnThem != null) btnThem.getStyle();
        if (btnTim != null) btnTim.getStyle();
        if (txtTimKiem != null) txtTimKiem.getPromptText();
        // Các cột text
        colMaSanPham.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMa()));
        colTenSanPham.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTen()));
        colDonGia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDonGia()));
        colMoTa.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMoTa()));
        // STT column: display row index + 1
        colSTT.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(tableSanPham.getItems().indexOf(cd.getValue()) + 1)));
        colSTT.setSortable(false);

        colAnh.setCellFactory(_col -> new TableCell<>() { // param renamed
            private final ImageView imageView = new ImageView();
            { imageView.setFitHeight(60); imageView.setFitWidth(60); imageView.setPreserveRatio(true); }
            @Override protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) { setGraphic(null); return; }
                try {
                    Image image = null;
                    if (imagePath.startsWith(RESOURCE_IMAGE_PREFIX)) {
                        var url = getClass().getResource(imagePath);
                        if (url != null) {
                            image = new Image(url.toExternalForm(), 60, 60, true, true);
                        } else {
                            Path fsPath = Path.of(RESOURCE_IMAGE_DIR).resolve(imagePath.substring(RESOURCE_IMAGE_PREFIX.length()));
                            if (Files.exists(fsPath)) image = new Image("file:" + fsPath, 60, 60, true, true);
                        }
                    } else {
                        image = new Image("file:" + imagePath, 60, 60, true, true);
                    }
                    setGraphic(image != null ? imageView : null);
                    if (image != null) imageView.setImage(image);
                } catch (Exception ex) { setGraphic(null); }
            }
        });
        colAnh.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHinhAnh()));

        tableSanPham.setItems(danhSach);
        loadDataFromDatabase();

        colThaoTac.setCellFactory(_col -> new TableCell<>() { // param renamed
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox hbox = new HBox(5, btnSua, btnXoa);
            {   btnSua.setOnAction(_ev -> editRow());
                btnXoa.setOnAction(_ev -> deleteRow()); }
            private void editRow() {
                SanPham sp = getTableView().getItems().get(getIndex());
                if (sp == null) return;
                txtMaSanPham.setText(sp.getMa());
                txtTenSanPham.setText(sp.getTen());
                txtDonGia.setText(String.valueOf(sp.getDonGia()));
                txtMoTa.setText(sp.getMoTa());
                if (sp.getHinhAnh() != null) {
                    Image image = null;
                    if (sp.getHinhAnh().startsWith(RESOURCE_IMAGE_PREFIX)) {
                        var url = getClass().getResource(sp.getHinhAnh());
                        if (url != null) image = new Image(url.toExternalForm());
                    }
                    if (image == null) image = new Image("file:" + sp.getHinhAnh());
                    imgSanPham.setImage(image);
                    selectedImagePath = sp.getHinhAnh();
                }
            }
            private void deleteRow() {
                SanPham sp = getTableView().getItems().get(getIndex());
                if (sp == null) return;
                String sql = "DELETE FROM MON WHERE maMon=?";
                try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, sp.getMa());
                    ps.executeUpdate();
                    danhSach.remove(sp);
                    showAlert("Thành công", "Đã xóa sản phẩm!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    logError("Xóa sản phẩm thất bại", e);
                    showAlert("Lỗi", "Không thể xóa sản phẩm!", Alert.AlertType.ERROR);
                }
            }
            @Override protected void updateItem(String item, boolean empty) { super.updateItem(item, empty); setGraphic(empty? null : hbox); }
        });
    }

    private void loadDataFromDatabase() {
        danhSach.clear();
        String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM MON";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("maMon");
                String ten = rs.getString("tenMon");
                double donGia = rs.getDouble("giaCa");
                String moTa = rs.getString("moTa");
                String hinhAnh = rs.getString("hinhAnh");
                danhSach.add(new SanPham(ma, ten, donGia, moTa, hinhAnh));
            }
        } catch (Exception e) {
            logError("Load dữ liệu sản phẩm", e);
            showAlert("Lỗi", "Không thể load dữ liệu từ SQL Server!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onThem() {
        // Lấy dữ liệu từ form
        String ma = txtMaSanPham.getText().trim();
        String ten = txtTenSanPham.getText().trim();
        String moTa = txtMoTa.getText().trim();
        double donGia;

        try {
            donGia = Double.parseDouble(txtDonGia.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Đơn giá phải là số hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        // Kết nối và xử lý database
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Kiểm tra sản phẩm đã tồn tại
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
                // Nếu chưa tồn tại → INSERT
                String insertSql = "INSERT INTO MON(maMon, tenMon, giaCa, moTa, hinhAnh) VALUES (?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setString(1, ma);
                ps.setString(2, ten);
                ps.setDouble(3, donGia);
                ps.setString(4, moTa);
                ps.setString(5, selectedImagePath);
                ps.executeUpdate();
                showAlert("Thành công", "Đã thêm sản phẩm mới!", Alert.AlertType.INFORMATION);
            }

            // Xóa form sau khi thao tác thành công
            clearForm();

            // Load lại dữ liệu TableView
            loadDataFromDatabase();

        } catch (Exception e) {
            logError("Thêm/cập nhật sản phẩm", e);
            showAlert("Lỗi", "Không thể lưu sản phẩm!", Alert.AlertType.ERROR);
        }
    }

    // Phương thức xóa form
    private void clearForm() {
        txtMaSanPham.clear();
        txtTenSanPham.clear();
        txtDonGia.clear();
        txtMoTa.clear();
        imgSanPham.setImage(null);
        selectedImagePath = null;
    }


    @FXML
    private void onSua() { // ensure wired in FXML
        SanPham sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) { showAlert("Thông báo", "Vui lòng chọn sản phẩm cần sửa!", Alert.AlertType.WARNING); return; }
        String ma = txtMaSanPham.getText(); String ten = txtTenSanPham.getText(); String moTa = txtMoTa.getText(); double donGia;
        try { donGia = Double.parseDouble(txtDonGia.getText()); } catch (NumberFormatException e) { showAlert("Lỗi", "Đơn giá phải là số hợp lệ!", Alert.AlertType.ERROR); return; }
        String sql = "UPDATE MON SET tenMon=?, giaCa=?, moTa=?, hinhAnh=? WHERE maMon=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten); ps.setDouble(2, donGia); ps.setString(3, moTa); ps.setString(4, selectedImagePath); ps.setString(5, ma);
            ps.executeUpdate();
            sp.setTen(ten); sp.setDonGia(donGia); sp.setMoTa(moTa); sp.setHinhAnh(selectedImagePath);
            tableSanPham.refresh();
            showAlert("Thành công", "Đã cập nhật sản phẩm!", Alert.AlertType.INFORMATION);
        } catch (Exception e) { logError("Sửa sản phẩm", e); showAlert("Lỗi", "Không thể sửa sản phẩm!", Alert.AlertType.ERROR); }
    }

    @FXML
    private void onXoa() { // ensure wired in FXML
        SanPham sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) { showAlert("Thông báo", "Vui lòng chọn sản phẩm cần xóa!", Alert.AlertType.WARNING); return; }
        String sql = "DELETE FROM MON WHERE maMon=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getMa()); ps.executeUpdate(); danhSach.remove(sp);
            showAlert("Thành công", "Đã xóa sản phẩm!", Alert.AlertType.INFORMATION);
        } catch (Exception e) { logError("Xóa sản phẩm", e); showAlert("Lỗi", "Không thể xóa sản phẩm!", Alert.AlertType.ERROR); }
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
            String path = sp.getHinhAnh();
            try {
                if (path != null && !path.isEmpty()) {
                    Image image = null;
                    if (path.startsWith(RESOURCE_IMAGE_PREFIX)) {
                        var url = getClass().getResource(path);
                        if (url != null) {
                            image = new Image(url.toExternalForm());
                        } else {
                            Path fsPath = Path.of(RESOURCE_IMAGE_DIR).resolve(path.substring(RESOURCE_IMAGE_PREFIX.length()));
                            if (Files.exists(fsPath)) {
                                image = new Image("file:" + fsPath);
                            }
                        }
                    } else {
                        image = new Image("file:" + path);
                    }
                    if (image != null) {
                        imgSanPham.setImage(image);
                        selectedImagePath = path;
                    }
                }
            } catch (Exception ignore) {}
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
            try {
                Path projectImageDir = Path.of(RESOURCE_IMAGE_DIR);
                if (!Files.exists(projectImageDir)) Files.createDirectories(projectImageDir);
                String originalName = file.getName();
                Path dest = projectImageDir.resolve(originalName);
                if (Files.exists(dest)) {
                    String base = originalName;
                    String ext = "";
                    int dot = originalName.lastIndexOf('.');
                    if (dot > 0) { base = originalName.substring(0, dot); ext = originalName.substring(dot); }
                    dest = projectImageDir.resolve(base + "_" + System.currentTimeMillis() + ext);
                }
                if (!file.getAbsolutePath().startsWith(projectImageDir.toAbsolutePath().toString())) {
                    Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    dest = file.toPath();
                }
                selectedImagePath = RESOURCE_IMAGE_PREFIX + dest.getFileName();
                var url = getClass().getResource(selectedImagePath);
                if (url != null) {
                    imgSanPham.setImage(new Image(url.toExternalForm()));
                } else {
                    imgSanPham.setImage(new Image("file:" + dest));
                }
            } catch (Exception ex) {
                logError("Chọn ảnh", ex);
                showAlert("Lỗi", "Không thể sao chép ảnh vào thư mục tài nguyên", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void logError(String context, Exception e) { System.err.println("[ERROR] " + context + ": " + e.getMessage()); }
}
