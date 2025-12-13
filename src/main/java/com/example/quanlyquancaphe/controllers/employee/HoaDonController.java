package com.example.quanlyquancaphe.controllers.employee;

import com.example.quanlyquancaphe.DAO.BanDAO;
import com.example.quanlyquancaphe.DAO.SanPhamDAO;
import com.example.quanlyquancaphe.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage; // Import Stage

import java.io.FileWriter; // Import FileWriter cho chức năng in file
import java.io.IOException; // Import IOException
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class HoaDonController {
    @FXML private ScrollPane scrollPane;
    @FXML private ListView<ItemHoaDon> listHoaDon;
    private ObservableList<ItemHoaDon> dsMon = FXCollections.observableArrayList();
    private VBox selectedBox;

    @FXML private Label tongTienLabel;
    @FXML private Label tienThuaLabel;
    @FXML private TextField tienKhachDuaField;
    @FXML private Label lblThongTinBan;
    @FXML private ComboBox<String> cbPhuongThuc;
    @FXML private VBox boxTienMat;

    private double tongTien = 0;
    private Ban banHienTai;
    private BanDAO banDAO = new BanDAO();
    private ChonBanController parentController;


    // PHƯƠNG THỨC SETTER CHO PARENT
    public void setParentController(ChonBanController parent) {
        this.parentController = parent;
    }

    // PHƯƠNG THỨC NHẬN DỮ LIỆU BÀN
    public void setData(Ban selectedBan) {
        this.banHienTai = selectedBan;

        if (banHienTai != null) {
            // Cập nhật Label trên giao diện
            if (lblThongTinBan != null) {
                lblThongTinBan.setText("Lập Hóa Đơn cho: " + banHienTai.getMaBan() + " (" + banHienTai.getViTri() + ")");
            } else {
                System.err.println("Lỗi: lblThongTinBan chưa được ánh xạ trong FXML.");
            }

            System.out.println("Hóa đơn được lập cho Bàn: " + banHienTai.getMaBan());
            // TODO: Nếu bàn có trạng thái "Có khách", load hóa đơn cũ.
        }
    }

    @FXML
    public void initialize() {
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(3);

        try {
            Connection conn = DatabaseConnection.getConnection();
            SanPhamDAO dao = new SanPhamDAO(conn);
            List<SanPham> danhSachSanPham = dao.getAllSanPham();

            for (SanPham sp : danhSachSanPham) {
                VBox box = createSanPhamBox(sp);
                box.setOnMouseClicked(e -> themVaoHoaDon(sp));
                tilePane.getChildren().add(box);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // === LOGIC XỬ LÝ SỰ KIỆN ĐÓNG CỬA SỔ ===
        Platform.runLater(() -> {
            Stage stage = (Stage) scrollPane.getScene().getWindow();

            stage.setOnCloseRequest(event -> {

                // 1. Nếu danh sách món ĐÃ CÓ (đã gọi món), CHẶN đóng cửa sổ
                if (!dsMon.isEmpty()) {
                    event.consume(); // Ngăn chặn sự kiện đóng cửa sổ mặc định

                    new Alert(Alert.AlertType.WARNING,
                            "Bạn đã thêm món vào hóa đơn. Vui lòng sử dụng nút 'Thanh toán' để kết thúc giao dịch.").show();

                } else {
                    // 2. Nếu danh sách món RỖNG (chưa gọi món nào), CHO PHÉP đóng

                    // === LOGIC QUAN TRỌNG: HOÀN TÁC TRẠNG THÁI BÀN ===
                    if (banHienTai != null && banDAO != null && parentController != null) {
                        // Cập nhật trạng thái bàn trong DB về "Trống"
                        if (banDAO.updateTrangThai(banHienTai.getMaBan(), "Trống")) {

                            // Cập nhật đối tượng Ban trong bộ nhớ và giao diện chính
                            banHienTai.setTrangThai("Trống");
                            parentController.loadData();
                            System.out.println("DEBUG: Đã hoàn tác Bàn " + banHienTai.getMaBan() + " về trạng thái 'Trống'.");

                        } else {
                            System.err.println("LỖI: Không thể hoàn tác trạng thái bàn trong DB.");
                        }
                    }

                    // Do không gọi event.consume(), cửa sổ sẽ đóng bình thường.
                }
            });
            // === THÊM SETUP PHƯƠNG THỨC THANH TOÁN ===
            cbPhuongThuc.getItems().addAll("Tiền mặt", "Chuyển khoản");
            cbPhuongThuc.setValue("Tiền mặt"); // Mặc định là Tiền mặt

            cbPhuongThuc.valueProperty().addListener((obs, oldVal, newVal) -> {
                // Ẩn/hiện trường Tiền Khách Đưa và Tiền Thừa
                boolean isCash = "Tiền mặt".equals(newVal);
                boxTienMat.setVisible(isCash);
                boxTienMat.setManaged(isCash); // Đảm bảo giao diện co lại

                // Nếu chuyển khoản, xóa trường tiền đưa
                if (!isCash) {
                    tienKhachDuaField.setText(String.format("%.0f", tongTien)); // Đặt bằng Tổng tiền
                    tienThuaLabel.setText("Tiền thừa: 0đ");
                } else {
                    tienKhachDuaField.setText("");
                }
            });
        });

        scrollPane.setContent(tilePane);
        scrollPane.setFitToWidth(true);

        listHoaDon.setItems(dsMon);
        listHoaDon.setCellFactory(param -> new ListCell<ItemHoaDon>() {
            @Override
            protected void updateItem(ItemHoaDon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    SanPham sp = item.getSanPham();

                    Label ten = new Label(sp.getTen());
                    ten.setPrefWidth(150);

                    // Tạo số lượng và nút xóa trước
                    TextField soLuongField = new TextField(String.valueOf(item.getSoLuong()));
                    soLuongField.setPrefWidth(40);
                    soLuongField.setAlignment(Pos.CENTER);
                    soLuongField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

                    Button btnTang = new Button("+");
                    Button btnGiam = new Button("-");
                    btnTang.setOnAction(e -> {
                        item.tangSoLuong();
                        soLuongField.setText(String.valueOf(item.getSoLuong()));
                        capNhatTongTien();
                    });
                    btnGiam.setOnAction(e -> {
                        item.giamSoLuong();
                        soLuongField.setText(String.valueOf(item.getSoLuong()));
                        capNhatTongTien();
                    });

                    soLuongField.textProperty().addListener((obs, oldVal, newVal) -> {
                        try {
                            int sl = Integer.parseInt(newVal);
                            if (sl > 0) {
                                item.setSoLuong(sl);
                                capNhatTongTien();
                            }
                        } catch (NumberFormatException ignored) {}
                    });

                    Button btnXoa = new Button("❌");
                    btnXoa.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
                    btnXoa.setOnAction(e -> {
                        dsMon.remove(item);
                        capNhatTongTien();
                    });

                    HBox soLuongBox = new HBox(5, btnGiam, soLuongField, btnTang);
                    soLuongBox.setAlignment(Pos.CENTER);

                    // Hiển thị giá
                    HBox box;
                    if (sp.getGiaKM() != null && sp.getGiaKM() > 0 && sp.getGiaKM() < sp.getDonGia()) {
                        Text giaGoc = new Text(String.format("%.0fđ", sp.getDonGia()));
                        giaGoc.setFill(Color.GRAY);
                        giaGoc.setStrikethrough(true);

                        Label giaKM = new Label(String.format("%.0fđ", sp.getGiaHienThi()));
                        giaKM.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");

                        VBox giaBox = new VBox(2, giaGoc, giaKM);
                        giaBox.setAlignment(Pos.CENTER_LEFT);
                        giaBox.setPrefWidth(80);

                        box = new HBox(20, ten, giaBox, soLuongBox, btnXoa);
                    } else {
                        Label gia = new Label(String.format("%.0fđ", sp.getGiaHienThi()));
                        gia.setStyle("-fx-text-fill: #000000;");
                        gia.setPrefWidth(80);

                        box = new HBox(20, ten, gia, soLuongBox, btnXoa);
                    }

                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setPadding(new Insets(5));
                    setGraphic(box);
                }
            }
        });

    }

    private VBox createSanPhamBox(SanPham sp) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("product-box");
        ImageView img = null;
        URL imgUrl = getClass().getResource(sp.getHinhAnh());
        if (imgUrl != null) {
            img = new ImageView(new Image(imgUrl.toExternalForm()));
            img.setFitWidth(100);
            img.setFitHeight(100);
            img.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 0, 2);");
            box.getChildren().add(img);
        }

        Label ten = new Label(sp.getTen());
        // Giá hiển thị
        if (sp.getGiaKM() != null && sp.getGiaKM() > 0 && sp.getGiaKM() < sp.getDonGia()) {
            Text giaGoc = new Text(String.format("%.0fđ", sp.getDonGia()));
            giaGoc.setFill(Color.BLACK);
            giaGoc.setStrikethrough(true);

            Label giaKM = new Label(String.format("%.0fđ", sp.getGiaKM()));
            giaKM.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");

            VBox giaBox = new VBox(2, giaGoc, giaKM);
            giaBox.setAlignment(Pos.CENTER);

            box.getChildren().addAll(ten, giaBox);
        } else {
            Label gia = new Label(String.format("%.0fđ", sp.getDonGia()));
            gia.setStyle("-fx-text-fill: #000000;");
            box.getChildren().addAll(ten, gia);
        }

        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        return box;
    }

    private void themVaoHoaDon(SanPham sp) {
        for (ItemHoaDon item : dsMon) {
            if (item.getSanPham().getMa() == sp.getMa()) {
                item.tangSoLuong();
                listHoaDon.refresh();
                capNhatTongTien();
                return;
            }
        }
        dsMon.add(new ItemHoaDon(sp, 1));
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        tongTien = 0;
        for (ItemHoaDon item : dsMon) {
            tongTien += item.getSanPham().getGiaHienThi() * item.getSoLuong();
        }
        tongTienLabel.setText("Tổng: " + String.format("%.0fđ", tongTien));
    }


    private int generateMaHoaDon(Connection conn) throws SQLException {
        String sql = "SELECT ISNULL(MAX(maHoaDon), 0) FROM HOADON";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1;
    }
    private void printHoaDonToTxt(int maHoaDon, double tienKhachDua, double tienThua) {
        String maNhanVien = (TaiKhoan.isUserLoggedIn() && TaiKhoan.getUserLoggedIn().getMaTaiKhoan() != null) ? TaiKhoan.getUserLoggedIn().getMaTaiKhoan() : "UNKNOWN";
        String phuongThuc = cbPhuongThuc.getValue(); // Lấy phương thức thanh toán

        try (FileWriter writer = new FileWriter("HoaDon_" + maHoaDon + ".txt")) {
            writer.write("================= COFFEE SHOP =================\n");
            writer.write("          HOÁ ĐƠN THANH TOÁN (BILL)\n");
            writer.write("Mã HĐ: " + maHoaDon + "\n");
            writer.write("Bàn: " + banHienTai.getMaBan() + " - NV: " + maNhanVien + "\n");
            writer.write("Ngày: " + LocalDate.now() + "\n");
            writer.write("Phương thức: " + phuongThuc + "\n"); // <<< THÊM DÒNG NÀY
            writer.write("-----------------------------------------------\n");
            writer.write(String.format("%-15s %-5s %10s\n", "Tên Món", "SL", "Thành Tiền"));
            writer.write("-----------------------------------------------\n");

            for (ItemHoaDon item : listHoaDon.getItems()) {
                double thanhTien = item.getSanPham().getGiaHienThi() * item.getSoLuong();
                writer.write(String.format("%-15s %-5d %10.0fđ\n",
                        item.getSanPham().getTen(),
                        item.getSoLuong(),
                        thanhTien));
            }
            writer.write("-----------------------------------------------\n");
            writer.write(String.format("%-21s %15.0fđ\n", "TỔNG CỘNG:", tongTien));

            // Chỉ in Tiền Khách Đưa và Tiền Thừa nếu là Tiền mặt
            if ("Tiền mặt".equals(phuongThuc)) {
                writer.write(String.format("%-21s %15.0fđ\n", "KHÁCH ĐƯA:", tienKhachDua));
                writer.write(String.format("%-21s %15.0fđ\n", "TIỀN THỪA:", tienThua));
            }

            writer.write("===============================================\n");
            System.out.println("DEBUG: Đã in hóa đơn " + maHoaDon + " ra file.");
        } catch (IOException e) {
            System.err.println("Lỗi khi in hóa đơn ra file TXT: " + e.getMessage());
        }
    }


    // Trong HoaDonController.java, phương thức handleThanhToan()

    @FXML
    private void handleThanhToan() {

        // Lấy Stage hiện tại để đóng cửa sổ
        Stage currentStage = (Stage) tienKhachDuaField.getScene().getWindow();
        Connection conn = null; // Khai báo Connection ở phạm vi cao hơn

        try {
            // Lấy phương thức thanh toán và mã PT
            String phuongThuc = cbPhuongThuc.getValue();
            int maPT = "Chuyển khoản".equals(phuongThuc) ? 2 : 1;

            double tienKhachDua = 0;

            if (maPT == 1) { // Tiền mặt
                try {
                    // Lấy tiền khách đưa và kiểm tra thiếu tiền
                    tienKhachDua = Double.parseDouble(tienKhachDuaField.getText());
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Vui lòng nhập số tiền hợp lệ vào trường 'Tiền khách đưa'.").showAndWait();
                    return;
                }
            } else { // Chuyển khoản
                tienKhachDua = tongTien; // Coi như khách chuyển khoản đủ tổng tiền
            }

            double tienThua = tienKhachDua - tongTien;

            // Kiểm tra thiếu tiền (Chỉ cần kiểm tra nếu là Tiền mặt)
            if (maPT == 1 && tienThua < 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Thiếu tiền");
                alert.setHeaderText("Khách đưa chưa đủ tiền");
                alert.setContentText("Khách cần đưa thêm: " + String.format("%.0fđ", -tienThua));
                alert.showAndWait();
                return;
            }

            // 0. KIỂM TRA TÍNH TOÀN VẸN DỮ LIỆU
            if (banHienTai == null || banHienTai.getMaBan().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: Không tìm thấy thông tin bàn để lập hóa đơn.").showAndWait();
                return;
            }

            if (!TaiKhoan.isUserLoggedIn() || TaiKhoan.getUserLoggedIn().getMaTaiKhoan() == null) {
                new Alert(Alert.AlertType.ERROR, "Không tìm thấy thông tin nhân viên lập hóa đơn. Vui lòng đăng nhập lại!").showAndWait();
                return;
            }

            if (dsMon.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ít nhất một món để thanh toán.").showAndWait();
                return;
            }


            // ✅ Xử lý DB
            try {
                conn = DatabaseConnection.getConnection(); // Gán giá trị cho conn
                conn.setAutoCommit(false);

                // 1. Insert HOADON
                int maHoaDon = generateMaHoaDon(conn);

                String sqlHoaDon = "INSERT INTO HOADON (maHoaDon, tongKM, tongTien, ngayLap,  maBan, maTaiKhoan, maPT) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psHoaDon = conn.prepareStatement(sqlHoaDon);
                psHoaDon.setInt(1, maHoaDon);
                psHoaDon.setDouble(2, 0); // tongKM
                psHoaDon.setDouble(3, tongTien);
                psHoaDon.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

                psHoaDon.setString(5, banHienTai.getMaBan());

                psHoaDon.setString(6, TaiKhoan.getUserLoggedIn().getMaTaiKhoan());

                psHoaDon.setInt(7, maPT); // <<< SỬ DỤNG MÃ PHƯƠNG THỨC THANH TOÁN MỚI
                psHoaDon.executeUpdate();

                // 2. Insert CHITIETHOADON
                String sqlCTHD = "INSERT INTO CHITIETHOADON (maMon, maHoaDon, soLuong) VALUES (?, ?, ?)";
                PreparedStatement psCTHD = conn.prepareStatement(sqlCTHD);

                for (ItemHoaDon item : listHoaDon.getItems()) {
                    SanPham sp = item.getSanPham();
                    int soLuong = item.getSoLuong();
                    psCTHD.setString(1, sp.getMa());
                    psCTHD.setInt(2, maHoaDon);
                    psCTHD.setInt(3, soLuong);
                    psCTHD.addBatch();
                }
                psCTHD.executeBatch();

                conn.commit();

                // === XỬ LÝ SAU KHI THÀNH CÔNG ===

                // 3. Cập nhật Trạng Thái Bàn về "Trống"
                banDAO.updateTrangThai(banHienTai.getMaBan(), "Trống");

                // 4. Cập nhật dữ liệu trên màn hình chọn bàn (Parent Controller)
                if (parentController != null) {
                    parentController.loadData();
                }

                // 5. In hóa đơn ra file TXT
                printHoaDonToTxt(maHoaDon, tienKhachDua, tienThua);

                // 6. Thông báo và đóng cửa sổ
                tienThuaLabel.setText("Tiền thừa: " + String.format("%.0fđ", tienThua));
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Thanh toán");
                infoAlert.setHeaderText("Hóa đơn đã được thanh toán (" + phuongThuc + ")");
                infoAlert.setContentText("Tổng tiền: " + String.format("%.0fđ", tongTien) + "\nTiền thừa: " + String.format("%.0fđ", tienThua));
                infoAlert.showAndWait();

                currentStage.close(); // Đóng cửa sổ chỉ khi thanh toán thành công

            } catch (Exception e) {
                // Xử lý Rollback nếu có lỗi DB
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                // Đóng kết nối (Đảm bảo an toàn)
                if (conn != null) {
                    conn.close();
                }
            }

            listHoaDon.getItems().clear();
            tongTien = 0;
            tongTienLabel.setText("Tổng: 0đ");
            tienKhachDuaField.clear();
            tienThuaLabel.setText("Tiền thừa: 0đ");

        } catch (NumberFormatException e) {
            // Lỗi này đã được xử lý cụ thể cho trường tiền mặt bên trong.
            // Đây là khối catch cho các lỗi khác.
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu hóa đơn");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Đã xảy ra lỗi không xác định");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }
}