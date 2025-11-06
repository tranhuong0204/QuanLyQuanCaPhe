package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import com.example.quanlyquancaphe.models.KhuyenMai;
import com.example.quanlyquancaphe.models.KhuyenMaiDAO;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KhuyenMaiController {
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
    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    // Table & columns
    @FXML private TableView<KhuyenMaiVM> tableKhuyenMai;
    @FXML private TableColumn<KhuyenMaiVM, String> colMa;
    @FXML private TableColumn<KhuyenMaiVM, String> colTen;
    @FXML private TableColumn<KhuyenMaiVM, Number> colPhanTram;
    @FXML private TableColumn<KhuyenMaiVM, LocalDate> colNgayBatDau;
    @FXML private TableColumn<KhuyenMaiVM, LocalDate> colNgayKetThuc;
    @FXML private TableColumn<KhuyenMaiVM, String> colGhiChu;

    private final ObservableList<KhuyenMaiVM> data = FXCollections.observableArrayList();
    private FilteredList<KhuyenMaiVM> filtered;
    private SortedList<KhuyenMaiVM> sorted;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final KhuyenMaiDAO dao = new KhuyenMaiDAO();

    @FXML
    private void initialize() {
        // Bind table columns
        colMa.setCellValueFactory(c -> c.getValue().ma);
        colTen.setCellValueFactory(c -> c.getValue().ten);
        colPhanTram.setCellValueFactory(c -> c.getValue().phanTram);
        colNgayBatDau.setCellValueFactory(c -> c.getValue().ngayBatDau);
        colNgayKetThuc.setCellValueFactory(c -> c.getValue().ngayKetThuc);
        colGhiChu.setCellValueFactory(c -> c.getValue().ghiChu);

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
                data.add(toVM(km));
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
            code = dao.nextCode(conn);
            KhuyenMai km = readFormAsEntity(code);
            if (km == null) return;
            dao.insert(conn, km);
        } catch (SQLException e) {
            showError("Lỗi thêm khuyến mãi", e);
            return;
        }
        // Update UI after DB success
        reloadFromDb();
        setNextCodeFromDb();
        clearFormExceptMa();
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
            KhuyenMai km = readFormAsEntity(sel.ma.get());
            if (km == null) return;
            dao.update(conn, km);
        } catch (SQLException e) {
            showError("Lỗi cập nhật khuyến mãi", e);
            return;
        }
        reloadFromDb();
        setNextCodeFromDb();
        clearFormExceptMa();
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

    // View-model for TableView (simple)
    public static class KhuyenMaiVM {
        final SimpleStringProperty ma = new SimpleStringProperty();
        final SimpleStringProperty ten = new SimpleStringProperty();
        final SimpleDoubleProperty phanTram = new SimpleDoubleProperty();
        final SimpleStringProperty ghiChu = new SimpleStringProperty();
        final SimpleObjectProperty<LocalDate> ngayBatDau = new SimpleObjectProperty<>();
        final SimpleObjectProperty<LocalDate> ngayKetThuc = new SimpleObjectProperty<>();

        public KhuyenMaiVM(String ma, String ten, double phanTram, String ghiChu, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
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
    }
}
