package com.example.quanlyquancaphe.models;

import java.util.Date;

public class HoaDon {
    private String maHoaDon;
    private int tongKM;
    private int tongTien;
    private Date ngayLap;
    private String phuongThucThanhToan;
    private String maBan;
    private String maTaiKhoan;
    private String maPT; // phương thức thanh toán

    public HoaDon() {}

    public HoaDon(String maHoaDon, int tongKM, int tongTien, Date ngayLap,
                  String phuongThucThanhToan, String maBan, String maTaiKhoan, String maPT) {
        this.maHoaDon = maHoaDon;
        this.tongKM = tongKM;
        this.tongTien = tongTien;
        this.ngayLap = ngayLap;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.maBan = maBan;
        this.maTaiKhoan = maTaiKhoan;
        this.maPT = maPT;
    }

    // getters + setters
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public int getTongKM() { return tongKM; }
    public void setTongKM(int tongKM) { this.tongKM = tongKM; }
    public int getTongTien() { return tongTien; }
    public void setTongTien(int tongTien) { this.tongTien = tongTien; }
    public Date getNgayLap() { return ngayLap; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }
    public String getMaBan() { return maBan; }
    public void setMaBan(String maBan) { this.maBan = maBan; }
    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }
    public String getMaPT() { return maPT; }
    public void setMaPT(String maPT) { this.maPT = maPT; }
}
