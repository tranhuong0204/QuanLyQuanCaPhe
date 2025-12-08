package com.example.quanlyquancaphe.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}

