package com.example.quanlyquancaphe.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = new HoaDon(
                        rs.getString("maHoaDon"),
                        rs.getInt("tongKM"),
                        rs.getBigDecimal("tongTien"),   // dùng BigDecimal
                        rs.getDate("ngayLap"),
                        //rs.getString("phuongThucThanhToan"),
                        rs.getString("maBan"),
                        rs.getString("maTaiKhoan"),
                        rs.getString("maPT")
                );
                list.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteHoaDon(String maHoaDon) {
        String sql = "DELETE FROM HOADON WHERE maHoaDon=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
