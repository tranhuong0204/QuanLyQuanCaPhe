package com.example.quanlyquancaphe.models;

import com.example.quanlyquancaphe.models.SanPham;
import com.example.quanlyquancaphe.models.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoiMonDAO {

    // Hàm chuyển giá “25.000đ” → 25000
    private double convertGia(String str) {
        try {
            str = str.replaceAll("[^0-9]", ""); // bỏ chữ, ký tự
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public List<SanPham> getAllMon() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM MON";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double gia = convertGia(rs.getString("giaCa"));
                list.add(new SanPham(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        gia,
                        rs.getString("moTa"),
                        rs.getString("hinhAnh")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
