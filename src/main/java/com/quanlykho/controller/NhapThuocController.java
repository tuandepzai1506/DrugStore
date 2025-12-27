package com.quanlykho.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class NhapThuocController {
    @FXML private TextField txtTenThuoc;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtHanSuDung;

    @FXML
    private void handleSave() {
        String ten = txtTenThuoc.getText();
        String sl = txtSoLuong.getText();
        
        if (ten.isEmpty() || sl.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng nhập đầy đủ thông tin!");
            alert.show();
        } else {
            System.out.println("Đã lưu: " + ten + " với số lượng: " + sl);
            // Tạm thời in ra console, bước sau chúng ta sẽ kết nối vào danh sách chung
        }
    }
}