package com.example.quanlyquancaphe.models;

import java.time.LocalDate;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private int giaTri; // %
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String moTa;

    public KhuyenMai(String maKM, String tenKM, int giaTri, LocalDate ngayBatDau, LocalDate ngayKetThuc, String moTa) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.giaTri = giaTri;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.moTa = moTa;
    }

    public String getMaKM() { return maKM; }
    public String getTenKM() { return tenKM; }
    public int getGiaTri() { return giaTri; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public String getMoTa() { return moTa; }

    public void setTenKM(String tenKM) { this.tenKM = tenKM; }
    public void setGiaTri(int giaTri) { this.giaTri = giaTri; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}

