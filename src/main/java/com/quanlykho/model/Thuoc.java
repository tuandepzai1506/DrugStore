package com.quanlykho.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Thuoc {

    private final SimpleStringProperty ten;
    private final SimpleIntegerProperty soLuong;
    private final SimpleStringProperty hanSuDung;

    public Thuoc(String ten, int soLuong, String hanSuDung) {
        this.ten = new SimpleStringProperty(ten);
        this.soLuong = new SimpleIntegerProperty(soLuong);
        this.hanSuDung = new SimpleStringProperty(hanSuDung);
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
}
