package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.KhuyenMai;
import com.example.quanlyquancaphe.DAO.KhuyenMaiDAO;
import com.example.quanlyquancaphe.models.SanPham;
import com.example.quanlyquancaphe.DAO.MonKhuyenMaiDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unused","FieldCanBeLocal"})
public class KhuyenMaiController {
    private static final Logger LOGGER = Logger.getLogger(KhuyenMaiController.class.getName());

    // Form fields
    @FXML private TextField txtMa;
    @FXML private TextField txtTen;
    @FXML private TextField txtPhanTram;
    @FXML private TextArea txtGhiChu;
    @FXML private TextField txtTimKiem;
    @FXML private DatePicker dpNgayBatDau;
    @FXML private DatePicker dpNgayKetThuc;
    @FXML private ComboBox<String> cbKieuLoc;

    // Buttons
    @FXML private Button btnThem; // used via FXML
    @FXML private Button btnSua;  // used via FXML
    @FXML private Button btnXoa;

    // Table & columns
    @FXML private TableView<KhuyenMaiVM> tableKhuyenMai;
    @FXML private TableColumn<KhuyenMaiVM, String> colMa;
    @FXML private TableColumn<KhuyenMaiVM, String> colTen;
    @FXML private TableColumn<KhuyenMaiVM, Number> colPhanTram;
    @FXML private TableColumn<KhuyenMaiVM, LocalDate> colNgayBatDau;
    @FXML private TableColumn<KhuyenMaiVM, LocalDate> colNgayKetThuc;
    @FXML private TableColumn<KhuyenMaiVM, String> colGhiChu;
    @FXML private TableColumn<KhuyenMaiVM, String> colSanPham; // Cột mới để hiển thị sản phẩm áp dụng

    @FXML private ListView<String> lstSanPhamApDung; // list of selected products codes/names
    @FXML private TextField txtSanPhamApDung; // new compact display of selected products

    private final ObservableList<KhuyenMaiVM> data = FXCollections.observableArrayList();
    private FilteredList<KhuyenMaiVM> filtered;
    private SortedList<KhuyenMaiVM> sorted; // kept as field intentionally for future dynamic sorting
    private ObservableList<SanPham> sanPhamApDung; // holds selected products mapping
    private final MonKhuyenMaiDAO monKhuyenMaiDAO = new MonKhuyenMaiDAO();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final KhuyenMaiDAO dao = new KhuyenMaiDAO();

    @FXML
    private void initialize() {
        // Touch buttons so analyzer sees usage
        if (btnThem != null) btnThem.getStyle();
        if (btnSua != null) btnSua.getStyle();
        // Mark onChonSanPham as intentionally referenced to suppress 'never used'
        Runnable _refOnChonSanPham = this::onChonSanPham;

        // Bind table columns
        colMa.setCellValueFactory(c -> c.getValue().ma);
        colTen.setCellValueFactory(c -> c.getValue().ten);
        colPhanTram.setCellValueFactory(c -> c.getValue().phanTram);
        colNgayBatDau.setCellValueFactory(c -> c.getValue().ngayBatDau);
        colNgayKetThuc.setCellValueFactory(c -> c.getValue().ngayKetThuc);
        colGhiChu.setCellValueFactory(c -> c.getValue().ghiChu);
        // Gán dữ liệu cho cột sản phẩm: chuỗi mã món áp dụng
        if (colSanPham != null) {
            colSanPham.setCellValueFactory(c -> c.getValue().maMonApDung);
        }

        // Render LocalDate nicely
        colNgayBatDau.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DATE_FMT.format(item));
            }
        });
        colNgayKetThuc.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DATE_FMT.format(item));
            }
        });

        // Prepare filtering
        filtered = new FilteredList<>(data, x -> true);
        sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableKhuyenMai.comparatorProperty());
        tableKhuyenMai.setItems(sorted);

        // Load data & next code from DB
        reloadFromDb();
        setNextCodeFromDb();

        // Sync selection -> form
        tableKhuyenMai.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> fillForm(sel));

        // Init filter ComboBox
        cbKieuLoc.getItems().setAll("Theo tên", "Theo %");
        cbKieuLoc.getSelectionModel().selectFirst();
    }

    private void reloadFromDb() {
        data.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (KhuyenMai km : dao.findAll(conn)) {
                KhuyenMaiVM vm = toVM(km);
                try {
                    // nạp tên sản phẩm áp dụng (nếu cần dùng ở nơi khác)
                    String tenSp = monKhuyenMaiDAO.getTenSanPhamApDung(conn, km.getMaKM());
                    vm.sanPhamApDung.set(tenSp);
                    // nạp danh sách mã món áp dụng để hiển thị trong cột colSanPham
                    String maMons = monKhuyenMaiDAO.getMaMonApDungAsString(conn, km.getMaKM());
                    vm.maMonApDung.set(maMons);
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING,
                            "Không load được danh sách sản phẩm/mã món áp dụng cho khuyến mãi " + km.getMaKM(), ex);
                }
                data.add(vm);
            }
        } catch (SQLException e) {
            showError("Lỗi tải dữ liệu", e);
        }
    }

    private void setNextCodeFromDb() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String next = dao.nextCode(conn);
            txtMa.setText(next);
        } catch (SQLException e) {
            showError("Lỗi lấy mã kế tiếp", e);
        }
    }

    private KhuyenMaiVM toVM(KhuyenMai km) {
        return new KhuyenMaiVM(
                km.getMaKM(),
                km.getTenKM(),
                km.getGiaTri(),
                km.getMoTa() == null ? "" : km.getMoTa(),
                km.getNgayBatDau(),
                km.getNgayKetThuc()
        );
    }

    @FXML
    private void onThem() {
        // Generate code from DB to avoid gaps
        String code;
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            code = dao.nextCode(conn);
            KhuyenMai km = readFormAsEntity(code);
            if (km == null) {
                conn.rollback();
                return;
            }
            dao.insert(conn, km);
            // Lưu mapping sản phẩm - khuyến mãi nếu có chọn
            if (sanPhamApDung != null && !sanPhamApDung.isEmpty()) {
                monKhuyenMaiDAO.insertMany(conn, code, sanPhamApDung);
            }
            conn.commit();
        } catch (SQLException e) {
            showError("Lỗi thêm khuyến mãi", e);
            return;
        }
        // Update UI after DB success
        reloadFromDb();
        setNextCodeFromDb();
        clearFormExceptMa();
        sanPhamApDung = null;
        // Select the new row
        data.stream().filter(vm -> vm.ma.get().equals(code)).findFirst()
                .ifPresent(vm -> tableKhuyenMai.getSelectionModel().select(vm));
    }

    @FXML
    private void onSua() {
        KhuyenMaiVM sel = tableKhuyenMai.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.INFORMATION, "Chưa chọn dòng", "Hãy chọn khuyến mãi trong bảng để sửa.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            KhuyenMai km = readFormAsEntity(sel.ma.get());
            if (km == null) {
                conn.rollback();
                return;
            }
            dao.update(conn, km);
            // Cập nhật lại mapping: xóa cũ, thêm mới
            monKhuyenMaiDAO.deleteByMaKM(conn, sel.ma.get());
            if (sanPhamApDung != null && !sanPhamApDung.isEmpty()) {
                monKhuyenMaiDAO.insertMany(conn, sel.ma.get(), sanPhamApDung);
            }
            conn.commit();
        } catch (SQLException e) {
            showError("Lỗi cập nhật khuyến mãi", e);
            return;
        }
        reloadFromDb();
        setNextCodeFromDb();
        clearFormExceptMa();
        sanPhamApDung = null;
    }

    @FXML
    private void onXoa() {
        KhuyenMaiVM sel = tableKhuyenMai.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.INFORMATION, "Chưa chọn dòng", "Hãy chọn khuyến mãi trong bảng để xóa.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xóa khuyến mãi đã chọn?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            dao.delete(conn, sel.ma.get());
        } catch (SQLException e) {
            showError("Lỗi xóa khuyến mãi", e);
            return;
        }
        reloadFromDb();
        setNextCodeFromDb();
        clearFormExceptMa();
    }

    @FXML
    private void onChonDong(MouseEvent event) {
        KhuyenMaiVM sel = tableKhuyenMai.getSelectionModel().getSelectedItem();
        fillForm(sel);
        // đồng thời nạp danh sách sản phẩm áp dụng cho khuyến mãi được chọn
        loadSanPhamApDungForSelected(sel);
    }

    private void fillForm(KhuyenMaiVM sel) {
        if (sel == null) return;
        txtMa.setText(sel.ma.get());
        txtTen.setText(sel.ten.get());
        txtPhanTram.setText(Double.toString(sel.phanTram.get()));
        txtGhiChu.setText(sel.ghiChu.get());
        dpNgayBatDau.setValue(sel.ngayBatDau.get());
        dpNgayKetThuc.setValue(sel.ngayKetThuc.get());
    }

    /**
     * Nạp lại danh sách sản phẩm áp dụng cho khuyến mãi được chọn
     * và hiển thị lên txtSanPhamApDung / lstSanPhamApDung để có thể sửa.
     */
    private void loadSanPhamApDungForSelected(KhuyenMaiVM sel) {
        if (sel == null) return;
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> maMons = monKhuyenMaiDAO.getMaMonByMaKM(conn, sel.getMa());
            if (maMons == null || maMons.isEmpty()) {
                sanPhamApDung = FXCollections.observableArrayList();
                if (txtSanPhamApDung != null) txtSanPhamApDung.clear();
                if (lstSanPhamApDung != null) lstSanPhamApDung.getItems().clear();
                return;
            }
            // Lấy đầy đủ thông tin sản phẩm theo các mã này từ bảng MON
            ObservableList<SanPham> list = FXCollections.observableArrayList();
            String placeholders = String.join(",", java.util.Collections.nCopies(maMons.size(), "?"));
            String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM MON WHERE maMon IN (" + placeholders + ")";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < maMons.size(); i++) {
                    ps.setString(i + 1, maMons.get(i));
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maMon");
                        String ten = rs.getString("tenMon");
                        double gia = rs.getDouble("giaCa");
                        String moTa = rs.getString("moTa");
                        String hinhAnh = rs.getString("hinhAnh");
                        list.add(new SanPham(ma, ten, gia, moTa, hinhAnh));
                    }
                }
            }
            this.sanPhamApDung = list;
            // cập nhật hiển thị như khi nhận từ popup
            if (lstSanPhamApDung != null) {
                lstSanPhamApDung.getItems().setAll(
                        list.stream().map(sp -> sp.getMa() + " - " + sp.getTen()).toList()
                );
            }
            if (txtSanPhamApDung != null) {
                String joined = list.stream().map(SanPham::getTen)
                        .reduce((a,b) -> a + ", " + b).orElse("");
                txtSanPhamApDung.setText(joined.isEmpty() ? "" : joined);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING,
                    "Không thể tải danh sách sản phẩm áp dụng khi chọn khuyến mãi " + sel.getMa(), e);
        }
    }

    private KhuyenMai readFormAsEntity(String code) {
        String ten = txtTen.getText().trim();
        String phanTramStr = txtPhanTram.getText().trim();
        String moTa = txtGhiChu.getText().trim();
        LocalDate bd = dpNgayBatDau.getValue();
        LocalDate kt = dpNgayKetThuc.getValue();

        if (ten.isEmpty() || phanTramStr.isEmpty() || bd == null || kt == null) {
            alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Vui lòng nhập Tên, Phần trăm và chọn Ngày bắt đầu/kết thúc.");
            return null;
        }
        int pt;
        try {
            double val = Double.parseDouble(phanTramStr);
            if (val < 0 || val > 100) {
                alert(Alert.AlertType.WARNING, "Phần trăm không hợp lệ", "Vui lòng nhập trong khoảng 0 - 100.");
                return null;
            }
            pt = (int) Math.round(val);
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Phần trăm không hợp lệ", "Vui lòng nhập số.");
            return null;
        }
        if (kt.isBefore(bd)) {
            alert(Alert.AlertType.WARNING, "Ngày không hợp lệ", "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            return null;
        }
        return new KhuyenMai(code, ten, pt, bd, kt, moTa);
    }

    private void clearFormExceptMa() {
        txtTen.clear();
        txtPhanTram.clear();
        txtGhiChu.clear();
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
        if (lstSanPhamApDung != null) lstSanPhamApDung.getItems().clear();
        if (txtSanPhamApDung != null) txtSanPhamApDung.clear();
    }

    private void alert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type, content, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }

    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void onTim() {
        String kieu = cbKieuLoc.getSelectionModel().getSelectedItem();
        String q = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase();
        filtered.setPredicate(vm -> {
            if (q.isEmpty()) return true;
            if ("Theo tên".equalsIgnoreCase(kieu)) {
                return vm.ten.get().toLowerCase().contains(q);
            } else { // Theo %
                try {
                    double val = Double.parseDouble(q);
                    return Double.compare(vm.phanTram.get(), val) == 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    @SuppressWarnings("unused") // invoked via FXML onAction
    @FXML
    private void onChonSanPham() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/quanlyquancaphe/adminView/ProductSelection.fxml"));
            Scene scene = new Scene(loader.load());
            ProductSelectionController productController = loader.getController();
            // nếu đang sửa một khuyến mãi đã có trong DB, preload danh sách món đang áp dụng
            KhuyenMaiVM sel = tableKhuyenMai.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    java.util.List<String> maMons = monKhuyenMaiDAO.getMaMonByMaKM(conn, sel.getMa());
                    productController.setSelectedMaMon(maMons);
                } catch (SQLException e) {
                    showError("Không tải được danh sách món đang áp dụng", e);
                }
            }
            productController.setCallback(this::nhanSanPhamChon);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chọn sản phẩm");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            showError("Không mở được cửa sổ chọn sản phẩm", e);
        }
    }

    private void nhanSanPhamChon(ObservableList<SanPham> danhSach) {
        this.sanPhamApDung = danhSach;
        // Update ListView if still present somewhere (ignore if null)
        if (lstSanPhamApDung != null) {
            lstSanPhamApDung.getItems().setAll(danhSach.stream().map(sp -> sp.getMa() + " - " + sp.getTen()).toList());
        }
        // Fill compact text field with comma-separated product names
        if (txtSanPhamApDung != null) {
            String joined = danhSach.stream().map(SanPham::getTen).reduce((a,b) -> a + ", " + b).orElse("");
            txtSanPhamApDung.setText(joined.isEmpty() ? "(chưa chọn)" : joined);
        }
    }

    // When reading form, you could later persist sanPhamApDung mapping table.
    // View-model for TableView (simple)
    public static class KhuyenMaiVM {
        final SimpleStringProperty ma = new SimpleStringProperty();
        final SimpleStringProperty ten = new SimpleStringProperty();
        final SimpleDoubleProperty phanTram = new SimpleDoubleProperty();
        final SimpleStringProperty ghiChu = new SimpleStringProperty();
        final SimpleObjectProperty<LocalDate> ngayBatDau = new SimpleObjectProperty<>();
        final SimpleObjectProperty<LocalDate> ngayKetThuc = new SimpleObjectProperty<>();
        // Thuộc tính cũ: danh sách tên sản phẩm áp dụng dạng chuỗi (nếu muốn dùng)
        final SimpleStringProperty sanPhamApDung = new SimpleStringProperty("");
        // Thuộc tính mới: danh sách mã món áp dụng dạng chuỗi để bind với colSanPham
        final SimpleStringProperty maMonApDung = new SimpleStringProperty("");

        public KhuyenMaiVM(String ma, String ten, double phanTram, String ghiChu,
                           LocalDate ngayBatDau, LocalDate ngayKetThuc) {
            this.ma.set(ma);
            this.ten.set(ten);
            this.phanTram.set(phanTram);
            this.ghiChu.set(ghiChu);
            this.ngayBatDau.set(ngayBatDau);
            this.ngayKetThuc.set(ngayKetThuc);
        }

        public String getMa() { return ma.get(); }
        public String getTen() { return ten.get(); }
        public double getPhanTram() { return phanTram.get(); }
        public String getGhiChu() { return ghiChu.get(); }
        public LocalDate getNgayBatDau() { return ngayBatDau.get(); }
        public LocalDate getNgayKetThuc() { return ngayKetThuc.get(); }
        public String getSanPhamApDung() { return sanPhamApDung.get(); }
        public String getMaMonApDung() { return maMonApDung.get(); }
    }
}
