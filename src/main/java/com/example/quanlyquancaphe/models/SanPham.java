package com.example.quanlyquancaphe.models;

public class SanPham {
    private String ma;
    private String ten;
    private double donGia;
    private String moTa;
    private String hinhAnh;
    private Double giaKM;// đường dẫn ảnh sản phẩm

    // Constructor đầy đủ
    public SanPham(String ma, String ten, double donGia, String moTa, String anh, Double giaKM) {
        this.ma = ma;
        this.ten = ten;
        this.donGia = donGia;
        this.moTa = moTa;
        this.hinhAnh = anh;
        this.giaKM = giaKM;
    }

    public SanPham(String ma, String ten, double donGia, String moTa, String anh) {
        this.ma = ma;
        this.ten = ten;
        this.donGia = donGia;
        this.moTa = moTa;
        this.hinhAnh = anh;
    }

    // Constructor không có ảnh (nếu chưa chọn ảnh)
    public SanPham(String ma, String ten, double donGia, String moTa) {
        this(ma, ten, donGia, moTa, null,null);
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

    public Double getGiaKM() {
        return giaKM;
    }

    public void setGiaKM(Double giaKM) {
        this.giaKM = giaKM;
    }

//    public double getGiaHienThi() {
//        return (giaKM != null && giaKM > 0 && giaKM < donGia) ? giaKM : donGia;
//    }
private KhuyenMai khuyenMai; // khuyến mãi áp dụng cho sản phẩm

//    public double getGiaHienThi() {
//        if (khuyenMai != null) {
//            return donGia * (1 - khuyenMai.getGiaTri() / 100.0);
//        }
//        return donGia;
//    }
    public double getGiaHienThi() {
        return (giaKM != null && giaKM > 0) ? giaKM : donGia;
    }



}

