package com.example.quanlyquancaphe.models;

public class TaiKhoan {
    private static String maTaiKhoan;
    private String tenTaiKhoan;
    private String matKhau;
    private String chucVu;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTaiKhoan, String tenTaiKhoan, String matKhau, String chucVu) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.chucVu = chucVu;
    }

    public static String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public static void setMaTaiKhoan(String maTaiKhoan) {
        TaiKhoan.maTaiKhoan = maTaiKhoan;
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
}
