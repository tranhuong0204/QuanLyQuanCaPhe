package com.example.quanlyquancaphe.models;

public class TaiKhoan {
    // 1. Thuộc tính của ĐỐI TƯỢNG (NON-STATIC)
    private String maTaiKhoan;
    private String tenTaiKhoan;
    private String matKhau;
    private String chucVu;

    // 2. Thuộc tính quản lý PHIÊN (STATIC)
    // Dùng để lưu trữ Tài Khoản của người đang đăng nhập.
    private static TaiKhoan userLoggedIn = null;

    // ==========================================================
    // Constructor
    // ==========================================================

    public TaiKhoan() {
    }

    public TaiKhoan(String maTaiKhoan, String tenTaiKhoan, String matKhau, String chucVu) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.chucVu = chucVu;
    }

    // ==========================================================
    // Getters/Setters NON-STATIC (cho dữ liệu đối tượng)
    // ==========================================================

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    // ==========================================================
    // Getters/Setters STATIC (cho quản lý phiên đăng nhập)
    // ==========================================================

    public static TaiKhoan getUserLoggedIn() {
        return userLoggedIn;
    }

    public static void setUserLoggedIn(TaiKhoan userLoggedIn) {
        TaiKhoan.userLoggedIn = userLoggedIn;
    }

    public static boolean isUserLoggedIn() {
        return userLoggedIn != null;
    }
}