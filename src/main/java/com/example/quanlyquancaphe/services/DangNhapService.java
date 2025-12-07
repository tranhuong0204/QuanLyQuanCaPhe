package com.example.quanlyquancaphe.services;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.models.TaiKhoanDAO;
import java.sql.ResultSet;

public class DangNhapService {
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public TaiKhoan login(String username, String password) {
        try {
            ResultSet rs = taiKhoanDAO.findByUsernameAndPassword(username, password);
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setMaTaiKhoan(rs.getString("maTaiKhoan"));
                tk.setTenTaiKhoan(rs.getString("tenTaiKhoan"));
                tk.setChucVu(rs.getString("chucVu"));
                // thêm các field khác nếu cần
                return tk;            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null; // không tìm thấy
    }
}
