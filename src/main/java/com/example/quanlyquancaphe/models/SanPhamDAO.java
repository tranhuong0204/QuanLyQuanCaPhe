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
}

