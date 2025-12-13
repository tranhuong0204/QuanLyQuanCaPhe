package com.example.quanlyquancaphe.DAO;

import com.example.quanlyquancaphe.models.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO cho bảng MON_KHUYENMAI (bảng nối N-N giữa MON và KHUYENMAI). */
public class MonKhuyenMaiDAO {

    /** Xóa toàn bộ mapping sản phẩm-khuyến mãi của một mã khuyến mãi. */
    public void deleteByMaKM(Connection conn, String maKM) throws SQLException {
        String sql = "DELETE FROM MON_KHUYENMAI WHERE maKM = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            ps.executeUpdate();
        }
    }

    /** Thêm danh sách sản phẩm áp dụng cho 1 khuyến mãi vào bảng MON_KHUYENMAI. */
    public void insertMany(Connection conn, String maKM, List<SanPham> sanPhams) throws SQLException {
        if (sanPhams == null || sanPhams.isEmpty()) return;
        String sql = "INSERT INTO MON_KHUYENMAI(maMon, maKM) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (SanPham sp : sanPhams) {
                if (sp == null || sp.getMa() == null) continue;
                ps.setString(1, sp.getMa());
                ps.setString(2, maKM);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Lấy danh sách mã món đang áp dụng cho một khuyến mãi. */
    public List<String> getMaMonByMaKM(Connection conn, String maKM) throws SQLException {
        String sql = "SELECT maMon FROM MON_KHUYENMAI WHERE maKM = ?";
        List<String> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maMon = rs.getString(1);
                    if (maMon != null && !maMon.isEmpty()) {
                        result.add(maMon);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Lấy danh sách tên sản phẩm áp dụng cho một mã khuyến mãi, nối thành 1 chuỗi để hiển thị.
     * Ví dụ: "Cà phê sữa, Trà sữa socola".
     */
    public String getTenSanPhamApDung(Connection conn, String maKM) throws SQLException {
        String sql = "SELECT sp.maMon FROM MON_KHUYENMAI mk " +
                     "JOIN MON sp ON mk.maMon = sp.maMon " +
                     "WHERE mk.maKM = ?";
        List<String> names = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ten = rs.getString(1);
                    if (ten != null && !ten.isEmpty()) {
                        names.add(ten);
                    }
                }
            }
        }
        if (names.isEmpty()) return "";
        return String.join(",", names);
    }

    /**
     * Lấy danh sách mã món áp dụng cho một mã khuyến mãi, nối thành 1 chuỗi để hiển thị trong cột.
     * Ví dụ: "M01, M02, M03".
     */
    public String getMaMonApDungAsString(Connection conn, String maKM) throws SQLException {
        List<String> maMons = getMaMonByMaKM(conn, maKM);
        if (maMons == null || maMons.isEmpty()) return "";
        return String.join(",", maMons);
    }
}
