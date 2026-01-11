package com.quanlykho.controller;

import com.quanlykho.database.StatisticsDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    // === HÔM NAY ===
    @FXML
    private Label lblTodayDate;

    @FXML
    private Label lblTodaysSalesQty;

    @FXML
    private Label lblTodaysRevenue;

    @FXML
    private Label lblTodaysImportQty;

    @FXML
    private Label lblTodaysImportValue;

    // === TỒNG KHO ===
    @FXML
    private Label lblTotalMedicineTypes;

    @FXML
    private Label lblTotalStockQty;

    @FXML
    private Label lblTotalStockValue;

    // === CẢNH BÁO ===
    @FXML
    private Label lblLowStockCount;

    @FXML
    private Label lblOutOfStockCount;

    @FXML
    private VBox containerStatistics;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // Hiển thị ngày hôm nay
            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy"));
            lblTodayDate.setText("Ngày: " + todayDate);

            // === HÔM NAY ===
            System.out.println("Đang tải thống kê hôm nay...");
            
            // Hàng bán hôm nay
            int todaysSalesQty = StatisticsDAO.getTodaysSaleQuantity();
            double todaysRevenue = StatisticsDAO.getTodaysRevenue();
            lblTodaysSalesQty.setText(todaysSalesQty + " cái");
            lblTodaysRevenue.setText(String.format("%,.0f VND", todaysRevenue));

            // Hàng nhập hôm nay
            int todaysImportQty = StatisticsDAO.getTodaysImportQuantity();
            double todaysImportValue = StatisticsDAO.getTodaysImportValue();
            lblTodaysImportQty.setText(todaysImportQty + " cái");
            lblTodaysImportValue.setText(String.format("%,.0f VND", todaysImportValue));

            // === TỒNG KHO ===
            System.out.println("Đang tải thống kê tồn kho...");
            
            int totalMedicineTypes = StatisticsDAO.getTotalMedicineTypes();
            int totalStockQty = StatisticsDAO.getTotalStockQuantity();
            double totalStockValue = StatisticsDAO.getTotalStockValue();
            
            lblTotalMedicineTypes.setText(totalMedicineTypes + " loại");
            lblTotalStockQty.setText(totalStockQty + " cái");
            lblTotalStockValue.setText(String.format("%,.0f VND", totalStockValue));

            // === CẢNH BÁO ===
            System.out.println("Đang tải cảnh báo tồn kho...");
            
            int lowStockCount = StatisticsDAO.getLowStockCount();
            int outOfStockCount = StatisticsDAO.getOutOfStockCount();
            
            // Hiển thị cảnh báo với màu
            lblLowStockCount.setText(lowStockCount + " loại");
            if (lowStockCount > 0) {
                lblLowStockCount.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Vàng
            }

            lblOutOfStockCount.setText(outOfStockCount + " loại");
            if (outOfStockCount > 0) {
                lblOutOfStockCount.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Đỏ
            }

            System.out.println("✓ Đã tải xong dữ liệu thống kê");

        } catch (Exception e) {
            System.err.println("✗ Lỗi tải thống kê: " + e.getMessage());
            e.printStackTrace();
            lblTotalMedicineTypes.setText("Lỗi kết nối database");
        }
    }

    /**
     * LOGIC: Làm mới dữ liệu (nếu người dùng click nút "Làm mới")
     */
    @FXML
    private void refreshStatistics() {
        System.out.println("Đang làm mới dữ liệu thống kê...");
        loadStatistics();
    }
}
