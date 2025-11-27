package com.example.quanlyquancaphe.models;

import com.example.quanlyquancaphe.models.*;
import com.example.quanlyquancaphe.models.DatabaseConnection;

import java.sql.*;
import java.util.List;

public class HoaDonDAO {

    public boolean insertHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HOADON VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hd.getMaHoaDon());
            ps.setInt(2, hd.getTongKM());
            ps.setInt(3, hd.getTongTien());
            ps.setDate(4, new java.sql.Date(hd.getNgayLap().getTime()));
            ps.setString(5, hd.getPhuongThucThanhToan());
            ps.setString(6, hd.getMaBan());
            ps.setString(7, hd.getMaTaiKhoan());
            ps.setString(8, hd.getMaPT());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean insertCTHD(List<ChiTietHoaDon> list) {
        String sql = "INSERT INTO CHITIETHOADON VALUES (?,?,?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (ChiTietHoaDon ct : list) {
                ps.setString(1, ct.getMaMon());
                ps.setString(2, ct.getMaHoaDon());
                ps.setInt(3, ct.getSoLuong());
                ps.addBatch();
            }

            ps.executeBatch();
            return true;

        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean capNhatTrangThaiBan(String maBan, String trangThai) {
        String sql = "UPDATE BAN SET trangThai=? WHERE maBan=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setString(2, maBan);
            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
