package com.example.quanlyquancaphe.controllers.admin;

import com.example.quanlyquancaphe.models.DatabaseConnection;
//import com.sun.javafx.css.StyleClassSet;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ThongKeController implements Initializable {
    @FXML
    private ComboBox<String> cbCheDo;

    @FXML
    private ComboBox<Integer> cbNam;
    @FXML
    private AreaChart<String, Number> areaChart;
    @FXML
    private AnchorPane chartContainer;

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
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu");

//        areaChart.getData().clear();
//        areaChart.layout();
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
        yAxis.setLabel("Doanh thu");

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
