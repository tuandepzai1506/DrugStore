package com.quanlykho.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Thuoc {

    private final SimpleStringProperty ten;
    private final SimpleIntegerProperty soLuong;
    private final SimpleStringProperty hanSuDung;
    private final SimpleDoubleProperty gia;
    private final SimpleStringProperty haCungCap;

    public Thuoc(String ten, int soLuong, String hanSuDung, double gia, String haCungCap) {
        this.ten = new SimpleStringProperty(ten);
        this.soLuong = new SimpleIntegerProperty(soLuong);
        this.hanSuDung = new SimpleStringProperty(hanSuDung);
        this.gia = new SimpleDoubleProperty(gia);
        this.haCungCap = new SimpleStringProperty(haCungCap);
    }

    public String getTen() {
        return ten.get();
    }

    public int getSoLuong() {
        return soLuong.get();
    }

    public String getHanSuDung() {
        return hanSuDung.get();
    }

    public double getGia() {
        return gia.get();
    }

    public String getHaCungCap() {
        return haCungCap.get();
    }
}
