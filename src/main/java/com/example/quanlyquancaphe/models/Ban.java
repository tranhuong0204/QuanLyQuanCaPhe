package com.example.quanlyquancaphe.models;

public class Ban {
    private String maBan;
    private String viTri;
    private int soGhe;
    private String trangThai;
    private String ghiChu;

    public Ban(){

    }

    public Ban(String maBan, String viTri, int soGhe, String trangThai, String ghiChu) {
        this.maBan = maBan;
        this.viTri = viTri;
        this.soGhe = soGhe;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

