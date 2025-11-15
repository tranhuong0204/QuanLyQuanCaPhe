package com.example.quanlyquancaphe.models;

import com.example.quanlyquancaphe.models.Ban;
import com.example.quanlyquancaphe.models.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {

    public List<Ban> getAll() {
        List<Ban> list = new ArrayList<>();

        String sql = "SELECT MaBan, ViTri, SoGhe, TrangThai, GhiChu FROM Ban";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Ban(
                        rs.getString("MaBan"),
                        rs.getString("ViTri"),
                        rs.getInt("SoGhe"),
                        rs.getString("TrangThai"),
                        rs.getString("GhiChu")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Ban b) {
        String sql = "INSERT INTO Ban (MaBan, ViTri, SoGhe, TrangThai, GhiChu) VALUES (?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getMaBan());
            ps.setString(2, b.getViTri());
            ps.setInt(3, b.getSoGhe());
            ps.setString(4, b.getTrangThai());
            ps.setString(5, b.getGhiChu());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Ban b) {
        String sql = "UPDATE Ban SET ViTri=?, SoGhe=?, TrangThai=?, GhiChu=? WHERE MaBan=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getViTri());
            ps.setInt(2, b.getSoGhe());
            ps.setString(3, b.getTrangThai());
            ps.setString(4, b.getGhiChu());
            ps.setString(5, b.getMaBan());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maBan) {
        String sql = "DELETE FROM Ban WHERE MaBan=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maBan);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
