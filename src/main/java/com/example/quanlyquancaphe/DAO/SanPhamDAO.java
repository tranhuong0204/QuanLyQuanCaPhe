package com.example.quanlyquancaphe.DAO;

import com.example.quanlyquancaphe.models.SanPham;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {
    private Connection conn;

    public SanPhamDAO(Connection conn) {
        this.conn = conn;
    }

    public List<SanPham> getAllSanPham() throws SQLException {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT sp.maMon, sp.tenMon, sp.giaCa, sp.moTa, sp.hinhAnh, " +
                "km.giaTri, " +
                "CASE WHEN km.maKM IS NOT NULL AND km.ngayBatDau <= GETDATE() AND km.ngayKetThuc >= GETDATE() " +
                "     THEN sp.giaCa * (1 - km.giaTri / 100.0) " +
                "     ELSE sp.giaCa END AS giaKM " +
                "FROM MON sp " +
                "LEFT JOIN MON_KHUYENMAI mk ON sp.maMon = mk.maMon " +
                "LEFT JOIN KHUYENMAI km ON mk.maKM = km.maKM";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getDouble("giaCa"),
                        rs.getString("moTa"),
                        rs.getString("hinhAnh"),
                        rs.getDouble("giaKM") // luôn có giá hiển thị (giảm hoặc gốc)
                );
                list.add(sp);
            }
        }
        return list;
    }

//    public List<SanPham> getAllSanPham() throws SQLException {
//        List<SanPham> list = new ArrayList<>();
//        String sql = "SELECT sp.maMon, sp.tenMon, sp.giaCa, sp.moTa, sp.hinhAnh, " +
//                "km.giaTri, (sp.giaCa * (1 - km.giaTri / 100.0)) AS giaKM " +
//                "FROM MON sp " +
//                "LEFT JOIN MON_KHUYENMAI mk ON sp.maMon = mk.maMon " +
//                "LEFT JOIN KHUYENMAI km ON mk.maKM = km.maKM " +
//                "WHERE km.ngayBatDau <= GETDATE() AND km.ngayKetThuc >= GETDATE()";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                SanPham sp = new SanPham(
//                        rs.getString("maMon"),
//                        rs.getString("tenMon"),
//                        rs.getDouble("giaCa"),
//                        rs.getString("moTa"),
//                        rs.getString("hinhAnh"),
//                        rs.getDouble("giaKM") // giá sau khuyến mãi tính từ join
//                );
//                list.add(sp);
//            }
//        }
//        return list;
//    }

    //    public List<SanPham> getAllSanPham() {
//        List<SanPham> list = new ArrayList<>();
//        String sql = "SELECT maMon, tenMon, giaCa, moTa, hinhAnh FROM mon";
//
//        try (Statement st = conn.createStatement();
//             ResultSet rs = st.executeQuery(sql)) {
//            while (rs.next()) {
//                SanPham sp = new SanPham(
//                        rs.getString("maMon"),
//                        rs.getString("tenMon"),
//                        rs.getDouble("giaCa"),
//                        rs.getString("moTa"),
//                        rs.getString("hinhAnh")
//                );
//                list.add(sp);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
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

