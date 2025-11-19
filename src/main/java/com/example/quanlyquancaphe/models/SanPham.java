package com.example.quanlyquancaphe.models;

public class SanPham {
    private String ma;
    private String ten;
    private double donGia;
    private String moTa;
    private String hinhAnh; // đường dẫn ảnh sản phẩm

    // Constructor đầy đủ
    public SanPham(String ma, String ten, double donGia, String moTa, String anh) {
        this.ma = ma;
        this.ten = ten;
        this.donGia = donGia;
        this.moTa = moTa;
        this.hinhAnh = anh;
    }

    // Constructor không có ảnh (nếu chưa chọn ảnh)
    public SanPham(String ma, String ten, double donGia, String moTa) {
        this(ma, ten, donGia, moTa, null);
    }

    // Getter
    public String getMa() { return ma; }
    public String getTen() { return ten; }
    public double getDonGia() { return donGia; }
    public String getMoTa() { return moTa; }
    public String getHinhAnh() { return hinhAnh; }

    // Setter
    public void setMa(String ma) { this.ma = ma; }
    public void setTen(String ten) { this.ten = ten; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public void setHinhAnh(String anh) { this.hinhAnh = anh; }
}

