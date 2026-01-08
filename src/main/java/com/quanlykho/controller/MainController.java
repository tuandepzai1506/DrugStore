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

    @FXML
    private void showSupplierView() {
        // TODO: Implement supplier view
        System.out.println("Nhà cung cấp - Chưa implement");
    }

    @FXML
    private void showStaffView() {
        // TODO: Implement staff view
        System.out.println("Nhân viên - Chưa implement");
    }

    /**
     * LOGIC: Hiển thị màn hình thống kê
     * - Load StatisticsView.fxml
     * - Controller tự động tải dữ liệu thống kê từ database
     */
    @FXML
    private void showStatisticsView() {
        loadView("StatisticsView.fxml");
    }

    // Thêm hàm thoát ứng dụng
    @FXML
    private void exitApp() {
        System.exit(0);
    }
}