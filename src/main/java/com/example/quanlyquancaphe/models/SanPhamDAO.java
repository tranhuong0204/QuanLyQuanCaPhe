package com.example.quanlyquancaphe.models;

import com.example.quanlyquancaphe.models.SanPham;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {
    private Connection conn;

    public SanPhamDAO(Connection conn) {
        this.conn = conn;
    }

    public List<SanPham> getAllSanPham() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM mon";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getDouble("giaCa"),
                        rs.getString("moTa"),
                        rs.getString("hinhAnh")
                );
                list.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static SanPham findByTen(String tenMon, Connection conn) {
        SanPham sp = null;
        String sql = "SELECT * FROM SANPHAM WHERE ten = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenMon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sp = new SanPham(
                        rs.getString("maMon"),
                        rs.getString("ten"),
                        rs.getDouble("donGia"),
                        rs.getString("hinhAnh")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sp;
    }

}

