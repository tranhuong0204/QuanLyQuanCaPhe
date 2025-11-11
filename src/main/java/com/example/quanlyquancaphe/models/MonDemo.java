package com.example.quanlyquancaphe.models;

public class MonDemo {
    private String tenMon;
    private String hinhAnh;
    private double giaCa;

    public MonDemo(String tenMon, String hinhAnh, double giaCa) {
        this.tenMon = tenMon;
        this.hinhAnh = hinhAnh;
        this.giaCa = giaCa;
    }

    // Getters
    public String getTenMon() { return tenMon; }
    public String getHinhAnh() { return hinhAnh; }
    public double getGiaCa() { return giaCa; }
}
