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
                        rs.getString("maTaiKhoan"),
                        rs.getString("tenTaiKhoan"),
                        rs.getString("matKhau"),
                        rs.getString("chucVu")
                ));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }
    public static boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM taikhoan WHERE tenTaiKhoan = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            return rs.next();  // true = đã tồn tại

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static TaiKhoan getById(String ma) {
        String sql = "SELECT * FROM taikhoan WHERE maTaiKhoan = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new TaiKhoan(
                        rs.getString("maTaiKhoan"),
                        rs.getString("tenTaiKhoan"),
                        rs.getString("matKhau"),
                        rs.getString("chucVu")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    public static boolean insert(TaiKhoan tk) {

        // Kiểm tra trùng tên tài khoản
        if (existsByUsername(tk.getTenTaiKhoan())) {
            System.out.println("Tên tài khoản đã tồn tại!");
            return false;
        }

        String sql = "INSERT INTO taikhoan (maTaiKhoan, tenTaiKhoan, matKhau, chucVu) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getTenTaiKhoan());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getChucVu());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean update(TaiKhoan tk) {
        String sql = "UPDATE taikhoan SET tenTaiKhoan=?, matKhau=?, chucVu=? WHERE maTaiKhoan=?";

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
        String sql = "DELETE FROM taikhoan WHERE maTaiKhoan=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ma);
            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }
    public static String generateNewId() {
        String sql = "SELECT MaTaiKhoan FROM TaiKhoan " +
                "WHERE MaTaiKhoan LIKE 'TK%' " +
                "ORDER BY CAST(SUBSTRING(MaTaiKhoan, 3, LEN(MaTaiKhoan)) AS INT) DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1); // ví dụ: TK007
                int number = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("TK%03d", number);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "TK001";
    }



    public TaiKhoan findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM taikhoan WHERE tenTaiKhoan = ? AND matKhau = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new TaiKhoan(
                        rs.getString("maTaiKhoan"),
                        rs.getString("tenTaiKhoan"),
                        rs.getString("matKhau"),
                        rs.getString("chucVu")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
