package com.example.quanlyquancaphe.services;

import com.example.quanlyquancaphe.models.TaiKhoan;
import com.example.quanlyquancaphe.models.TaiKhoanDAO;
import java.sql.ResultSet;

public class DangNhapService {
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public TaiKhoan login(String username, String password) {
        TaiKhoan tk = taiKhoanDAO.findByUsernameAndPassword(username, password);

        if (tk == null) {
            System.out.println("Sai tài khoản hoặc mật khẩu!");
            return null;
        }

        return tk;
    }

}
