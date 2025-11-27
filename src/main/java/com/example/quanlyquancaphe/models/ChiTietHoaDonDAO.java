package com.example.quanlyquancaphe.models;

import java.sql.*;

public class ChiTietHoaDonDAO {

    public boolean insert(ChiTietHoaDon ct) {
        String sql = "INSERT INTO CHITIETHOADON(MONmaMon, HOADONmaHoaDon, soLuong) VALUES(?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ct.getMaMon());
            ps.setString(2, ct.getMaHoaDon());
            ps.setInt(3, ct.getSoLuong());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
