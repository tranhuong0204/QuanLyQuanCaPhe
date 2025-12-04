package com.example.quanlyquancaphe.services;

import com.example.quanlyquancaphe.models.TaiKhoanDAO;
import java.sql.ResultSet;

public class DangNhapService {
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public String login(String username, String password) {
        try {
            ResultSet rs = taiKhoanDAO.findByUsernameAndPassword(username, password);
            if (rs.next()) {
                return rs.getString("chucVu"); // trả về role
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null; // không tìm thấy
    }
}
