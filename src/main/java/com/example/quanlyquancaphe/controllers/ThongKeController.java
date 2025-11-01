package com.example.quanlyquancaphe.controllers;

import com.example.quanlyquancaphe.models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ThongKeController implements Initializable {
    @FXML
    private ComboBox<String> cbCheDo;

    @FXML
    private ComboBox<Integer> cbNam;
    @FXML
    private AreaChart<String, Number> areaChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //cbCheDo.getItems().addAll("Theo năm", "Theo tháng");
        cbNam.getItems().addAll(2023, 2024, 2025); // hoặc load từ DB
        cbCheDo.setValue("Theo năm");
        veBieuDoTheoNam();
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

    private void veBieuDoTheoThang(int nam) {
        CategoryAxis xAxis = (CategoryAxis) areaChart.getXAxis();
        xAxis.setLabel("Tháng");
//        xAxis.setTickLabelRotation(0); // giữ nhãn ngang
//        xAxis.setTickLabelsVisible(true); // ép hiển thị nhãn
//        xAxis.setTickMarkVisible(true);

        NumberAxis yAxis = (NumberAxis) areaChart.getYAxis();
        yAxis.setLabel("Doanh thu");

        areaChart.getData().clear();
        areaChart.layout();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu năm " + nam);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT MONTH(ngayLap) AS thang, SUM(tongTien) AS doanhThu FROM HoaDon WHERE YEAR(ngayLap) = ? GROUP BY MONTH(ngayLap) ORDER BY thang")) {

            stmt.setInt(1, nam);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String thang = "Tháng " + String.valueOf(rs.getInt("thang"));
                double doanhThu = rs.getDouble("doanhThu");
                series.getData().add(new XYChart.Data<>(thang, doanhThu));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        areaChart.getData().add(series);
    }

    private void veBieuDoTheoNam() {
        // Cấu hình trục X và Y trước khi truy vấn
        CategoryAxis xAxis = (CategoryAxis) areaChart.getXAxis();
        xAxis.setLabel("Năm");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu");

        areaChart.getData().clear();
        areaChart.layout();
        //LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu theo năm");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT YEAR(ngayLap) AS nam, SUM(tongTien) AS doanhThu FROM HoaDon GROUP BY YEAR(ngayLap) ORDER BY nam")) {

            while (rs.next()) {

                String nam = String.valueOf(rs.getInt("nam"));
                double doanhThu = rs.getDouble("doanhThu");
                series.getData().add(new XYChart.Data<>(nam, doanhThu));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        areaChart.getData().add(series);
    }

    @FXML
    private void thongKeDoanhThu(ActionEvent event) {
        System.out.println("Đã nhấn vào SẢN PHẨM");
    }
}
