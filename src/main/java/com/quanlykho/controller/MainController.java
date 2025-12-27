package com.quanlykho.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

public class MainController {

    @FXML
    private StackPane contentPane;

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/quanlykho/fxml/" + fxmlFile)
            );
            Node view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            System.err.println("❌ Không load được: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void showImportView() {
        loadView("NhapThuocView.fxml");
    }

    @FXML
    private void showExportView() {
        loadView("BanThuocView.fxml");
    }

    // Hàm này tương ứng với nút "Tồn kho" trong FXML
    @FXML
    private void showInventoryView() {
        loadView("KhoThuocView.fxml");
    }

    // Thêm hàm này để hết lỗi showSupplierView
    @FXML
    private void showSupplierView() {
        System.out.println("➡ Mở giao diện Nhà cung cấp (Chưa có FXML)");
        // loadView("SupplierView.fxml"); // Mở comment khi bạn đã tạo file này
    }

    // Thêm hàm này để khớp với nút "Nhân viên"
    @FXML
    private void showStaffView() {
        System.out.println("➡ Mở giao diện Nhân viên");
    }

    // Thêm hàm thoát ứng dụng
    @FXML
    private void exitApp() {
        System.exit(0);
    }
}