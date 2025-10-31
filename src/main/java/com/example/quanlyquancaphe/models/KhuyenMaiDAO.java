package com.example.quanlyquancaphe.models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {
    public List<KhuyenMai> findAll(Connection conn) throws SQLException {
        String sql = "SELECT maKM, tenKM, giaTri, ngayBatDau, ngayKetThuc, moTa FROM KHUYENMAI ORDER BY maKM";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<KhuyenMai> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        }
    }

    public String nextCode(Connection conn) throws SQLException {
        // Compute next code from MAX(maKM) like KM001 -> KM002
        String sql = "SELECT MAX(maKM) FROM KHUYENMAI";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            String max = null;
            if (rs.next()) max = rs.getString(1);
            int next = 1;
            if (max != null && max.matches("KM\\d{3,}")) {
                next = Integer.parseInt(max.substring(2)) + 1;
            }
            return String.format("KM%03d", next);
        }
    }

    public void insert(Connection conn, KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO KHUYENMAI(maKM, tenKM, giaTri, ngayBatDau, ngayKetThuc, moTa) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setInt(3, km.getGiaTri());
            ps.setDate(4, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(5, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(6, km.getMoTa());
            ps.executeUpdate();
        }
    }

    public void update(Connection conn, KhuyenMai km) throws SQLException {
        String sql = "UPDATE KHUYENMAI SET tenKM=?, giaTri=?, ngayBatDau=?, ngayKetThuc=?, moTa=? WHERE maKM=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, km.getTenKM());
            ps.setInt(2, km.getGiaTri());
            ps.setDate(3, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(5, km.getMoTa());
            ps.setString(6, km.getMaKM());
            ps.executeUpdate();
        }
    }

    public void delete(Connection conn, String maKM) throws SQLException {
        String sql = "DELETE FROM KHUYENMAI WHERE maKM=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            ps.executeUpdate();
        }
    }

    private KhuyenMai map(ResultSet rs) throws SQLException {
        String maKM = rs.getString("maKM");
        String tenKM = rs.getString("tenKM");
        int giaTri = rs.getInt("giaTri");
        LocalDate bd = rs.getDate("ngayBatDau").toLocalDate();
        LocalDate kt = rs.getDate("ngayKetThuc").toLocalDate();
        String moTa = rs.getString("moTa");
        return new KhuyenMai(maKM, tenKM, giaTri, bd, kt, moTa);
    }
}

