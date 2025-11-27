package com.example.quanlyquancaphe.models;

public class Session {
    public static int maTaiKhoan; // hoặc lưu cả đối tượng TaiKhoan

    public static int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public static void setMaTaiKhoan(int maTaiKhoan) {
        Session.maTaiKhoan = maTaiKhoan;
    }
}
