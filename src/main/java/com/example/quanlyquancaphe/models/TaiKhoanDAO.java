package com.example.quanlyquancaphe.models;

import com.example.quanlyquancaphe.models.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    public static List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM taikhoan";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TaiKhoan(
                        rs.getString("MaTaiKhoan"),
                        rs.getString("TenTaiKhoan"),
                        rs.getString("MatKhau"),
                        rs.getString("ChucVu")
                ));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }

    public static TaiKhoan getById(String ma) {
        String sql = "SELECT * FROM taikhoan WHERE MaTaiKhoan = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new TaiKhoan(
                        rs.getString("MaTaiKhoan"),
                        rs.getString("TenTaiKhoan"),
                        rs.getString("MatKhau"),
                        rs.getString("ChucVu")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    public static boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO taikhoan VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getTenTaiKhoan());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getChucVu());

            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }

    public static boolean update(TaiKhoan tk) {
        String sql = "UPDATE taikhoan SET TenTaiKhoan=?, MatKhau=?, ChucVu=? WHERE MaTaiKhoan=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tk.getTenTaiKhoan());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getChucVu());
            ps.setString(4, tk.getMaTaiKhoan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }

    public static boolean delete(String ma) {
        String sql = "DELETE FROM taikhoan WHERE MaTaiKhoan=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ma);
            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }
    public static String generateNewId() {
        String sql = "SELECT TOP 1 maTaiKhoan FROM TaiKhoan ORDER BY maTaiKhoan DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);

                if (lastId == null) return "TK001";

                lastId = lastId.trim(); // quan trọng

                // Nếu sai định dạng thì reset về TK001
                if (!lastId.matches("TK\\d{3}")) {
                    return "TK001";
                }

                int number = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("TK%03d", number);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "TK001";
    }



}
