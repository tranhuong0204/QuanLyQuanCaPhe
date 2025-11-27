package com.example.quanlyquancaphe.models;

public class ItemHoaDon {
    private SanPham sanPham;
    private int soLuong;

    public ItemHoaDon(SanPham sanPham, int soLuong) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void tangSoLuong() {
        this.soLuong++;
    }

    public void giamSoLuong() {
        if (this.soLuong > 1) this.soLuong--;
    }

    public void setSoLuong(int sl) {
        this.soLuong = sl;
    }
}
