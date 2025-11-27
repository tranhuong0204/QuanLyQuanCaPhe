package com.example.quanlyquancaphe.models;

public class ChiTietHoaDon {
    private String maMon;
    private String maHoaDon;
    private int soLuong;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(String maMon, String maHoaDon, int soLuong) {
        this.maMon = maMon;
        this.maHoaDon = maHoaDon;
        this.soLuong = soLuong;
    }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}
