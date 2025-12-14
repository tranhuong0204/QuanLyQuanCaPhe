package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
//import com.sun.javafx.css.StyleClassSet;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;


import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThongKeController implements Initializable {
    @FXML
    private ComboBox<String> cbCheDo;

    @FXML
    private ComboBox<Integer> cbNam;
    @FXML
    private AreaChart<String, Number> areaChart;
    @FXML
    private AnchorPane chartContainer;

    @FXML private TableView<Object[]> tableSanPham;
    @FXML private TableColumn<Object[], String> colTenSP;
    @FXML private TableColumn<Object[], Number> colSoLuong;
    @FXML private TableColumn<Object[], Number> colDoanhThu;
    @FXML private BarChart<String, Number> barChartSP;

    @FXML private ComboBox<Integer> cbNamSP;
    @FXML private ComboBox<Integer> cbThangSP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //cbCheDo.getItems().addAll("Theo năm", "Theo tháng");
        cbNam.getItems().addAll(2023, 2024, 2025); // hoặc load từ DB
        cbCheDo.setValue("Theo năm");
        veBieuDoTheoNam();
        loadSanPhamBanChay();
        loadNamFromDb();
        loadNamSPFromDb();
        cbThangSP.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList())
        ));

        // sự kiện chọn năm/tháng
        cbNamSP.setOnAction(e -> filterSanPhamBanChay());
        cbThangSP.setOnAction(e -> filterSanPhamBanChay());
    }

    private void loadNamSPFromDb() {
        List<Integer> years = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT DISTINCT YEAR(ngayLap) AS nam FROM HOADON ORDER BY nam")) {
            while (rs.next()) {
                years.add(rs.getInt("nam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cbNamSP.setItems(FXCollections.observableArrayList(years));
    }

    private void filterSanPhamBanChay() {
        Integer nam = cbNamSP.getValue();
        Integer thang = cbThangSP.getValue();

        if (nam != null && thang != null) {
            loadSanPhamBanChayTheoThang(nam, thang);
        } else if (nam != null) {
            loadSanPhamBanChayTheoNam(nam);
        } else {
            loadSanPhamBanChay(); // mặc định lấy tất cả
        }
    }

    private void loadSanPhamBanChayTheoNam(int nam) {
        ObservableList<Object[]> data = FXCollections.observableArrayList();

        String sql = "SELECT TOP 5 sp.tenMon, SUM(ct.soLuong) AS tongSL, SUM(ct.soLuong * sp.giaCa) AS tongDT " +
                "FROM CHITIETHOADON ct " +
                "JOIN MON sp ON ct.maMon = sp.maMon " +
                "JOIN HOADON hd ON ct.maHoaDon = hd.maHoaDon " +
                "WHERE YEAR(hd.ngayLap) = ? " +
                "GROUP BY sp.tenMon ORDER BY tongSL DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nam);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("tenMon"),
                        rs.getInt("tongSL"),
                        rs.getDouble("tongDT")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableSanPham.setItems(data);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top sản phẩm bán chạy năm " + nam);
        for (Object[] row : data) {
            series.getData().add(new XYChart.Data<>((String) row[0], (Integer) row[1]));
        }
        barChartSP.getData().setAll(series);
    }

    private void loadSanPhamBanChayTheoThang(int nam, int thang) {
        ObservableList<Object[]> data = FXCollections.observableArrayList();

        String sql = "SELECT TOP 5 sp.tenMon, SUM(ct.soLuong) AS tongSL, SUM(ct.soLuong * sp.giaCa) AS tongDT " +
                "FROM CHITIETHOADON ct " +
                "JOIN MON sp ON ct.maMon = sp.maMon " +
                "JOIN HOADON hd ON ct.maHoaDon = hd.maHoaDon " +
                "WHERE YEAR(hd.ngayLap) = ? AND MONTH(hd.ngayLap) = ? " +
                "GROUP BY sp.tenMon ORDER BY tongSL DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nam);
            ps.setInt(2, thang);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("tenMon"),
                        rs.getInt("tongSL"),
                        rs.getDouble("tongDT")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableSanPham.setItems(data);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top sản phẩm bán chạy tháng " + thang + "/" + nam);
        for (Object[] row : data) {
            series.getData().add(new XYChart.Data<>((String) row[0], (Integer) row[1]));
        }
        barChartSP.getData().setAll(series);
    }

    private void loadSanPhamBanChay() {
        ObservableList<Object[]> data = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT TOP 5 sp.tenMon, SUM(ct.soLuong) AS tongSL, SUM(ct.soLuong * sp.giaCa) AS tongDT " +
                             "FROM CHITIETHOADON ct " +
                             "JOIN MON sp ON ct.maMon = sp.maMon " +
                             "JOIN HOADON hd ON ct.maHoaDon = hd.maHoaDon " +
                             "GROUP BY sp.tenMon ORDER BY tongSL DESC ")) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("tenMon"),
                        rs.getInt("tongSL"),
                        rs.getDouble("tongDT")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Map dữ liệu vào TableView
        colTenSP.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty((String) cellData.getValue()[0]));
        colSoLuong.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty((Integer) cellData.getValue()[1]));
        colDoanhThu.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty((Double) cellData.getValue()[2]));

        tableSanPham.setItems(data);

        // Vẽ BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top sản phẩm bán chạy");
        for (Object[] row : data) {
            series.getData().add(new XYChart.Data<>((String) row[0], (Integer) row[1]));
        }
        barChartSP.getData().setAll(series);
    }

    @FXML
    private void chonCheDo(ActionEvent event) {
        String cheDo = cbCheDo.getValue();
        if ("Theo tháng".equals(cheDo)) {
            cbNam.setVisible(true);
            cbNam.setManaged(true);
//            int nam = cbNam.getValue();
//            veBieuDoTheoThang(nam);
        } else {
            cbNam.setVisible(false);
            cbNam.setManaged(false);
            veBieuDoTheoNam();
        }
    }

    @FXML
    private void chonNam(ActionEvent event) {
        Integer nam = cbNam.getValue();
        if (nam != null) {
            veBieuDoTheoThang(nam);
        }
    }

    private void loadNamFromDb() {
        List<Integer> years = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT DISTINCT YEAR(ngayLap) AS nam FROM HOADON ORDER BY nam")) {

            while (rs.next()) {
                years.add(rs.getInt("nam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cbNam.setItems(FXCollections.observableArrayList(years));
    }


    private void veBieuDoTheoThang(int nam) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu");

        AreaChart<String, Number> newChart = new AreaChart<>(xAxis, yAxis);
        newChart.setTitle("Doanh thu năm " + nam);
        newChart.setPrefWidth(900);
        newChart.setPrefHeight(500);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        //series.setName("Doanh thu năm " + nam);

        List<String> danhSachThang = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT MONTH(ngayLap) AS thang, SUM(tongTien) AS doanhThu FROM HoaDon WHERE YEAR(ngayLap) = ? GROUP BY MONTH(ngayLap) ORDER BY thang")) {

            stmt.setInt(1, nam);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String thang = "Tháng " + String.valueOf(rs.getInt("thang"));
                double doanhThu = rs.getDouble("doanhThu");
                danhSachThang.add(thang);
                series.getData().add(new XYChart.Data<>(thang, doanhThu));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        xAxis.setCategories(FXCollections.observableArrayList(danhSachThang));
        newChart.getData().add(series);

        chartContainer.getChildren().setAll(newChart); // gán biểu đồ mới vào giao diện
    }

    private void veBieuDoTheoNam() {
        // Cấu hình trục X và Y trước khi truy vấn
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Năm");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu (nghìn đồng)");

        AreaChart<String, Number> newChart = new AreaChart<>(xAxis, yAxis);
        newChart.setTitle("Doanh thu theo năm");
        newChart.setPrefWidth(900);
        newChart.setPrefHeight(500);

//        newChart.getStylesheets().add(getClass().getResource("/com/example/quanlyquancaphe/ThongKe.css").toExternalForm());
//        newChart.getStyleClass().add("area-chart-custom");

        //LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu theo năm");
        List<String> danhSachNam = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT YEAR(ngayLap) AS nam, SUM(tongTien) AS doanhThu FROM HoaDon GROUP BY YEAR(ngayLap) ORDER BY nam")) {

            while (rs.next()) {

                String nam = String.valueOf(rs.getInt("nam"));
                double doanhThu = rs.getDouble("doanhThu");
                danhSachNam.add(nam);
                series.getData().add(new XYChart.Data<>(nam, doanhThu));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        xAxis.setCategories(FXCollections.observableArrayList(danhSachNam));
        newChart.getData().add(series);

        chartContainer.getChildren().setAll(newChart);
    }


}
