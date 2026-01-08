package com.quanlykho.controller;

import com.quanlykho.database.StockOutDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BaoCaoController implements Initializable {

    @FXML
    private TextArea textAreaBaoCao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // LOGIC: Khởi tạo màn hình báo cáo
        // 1. Tải dữ liệu doanh thu từ database
        // 2. Format và hiển thị trong TextArea
        
        loadBaoCao();
    }

    /**
     * LOGIC: Tải báo cáo doanh thu
     * - Lấy doanh thu theo ngày từ view_daily_revenue
     * - Format dữ liệu để hiển thị dễ đọc
     * - Tính tổng cộng doanh thu
     * - Hiển thị trong TextArea
     */
    private void loadBaoCao() {
        try {
            StringBuilder report = new StringBuilder();
            report.append("=" .repeat(50)).append("\n");
            report.append("BÁO CÁO DOANH THU THEO NGÀY\n");
            report.append("=" .repeat(50)).append("\n\n");
            report.append("Ngày lập báo cáo: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

            // Lấy doanh thu từ database
            var revenueList = StockOutDAO.getDailyRevenue();
            
            if (revenueList.isEmpty()) {
                report.append("Không có dữ liệu bán hàng\n");
            } else {
                report.append("CHI TIẾT DOANH THU:\n");
                report.append("-".repeat(50)).append("\n");
                report.append(String.format("%-20s | %s\n", "Ngày", "Doanh thu"));
                report.append("-".repeat(50)).append("\n");

                double totalRevenue = 0;
                for (String revenue : revenueList) {
                    report.append(revenue).append("\n");
                    
                    // Tính tổng (lấy số từ chuỗi)
                    try {
                        String[] parts = revenue.split(": ");
                        if (parts.length > 1) {
                            String amountStr = parts[1].replaceAll("[^0-9]", "");
                            if (!amountStr.isEmpty()) {
                                totalRevenue += Long.parseLong(amountStr);
                            }
                        }
                    } catch (Exception e) {
                        // Bỏ qua nếu parse thất bại
                    }
                }

                report.append("-".repeat(50)).append("\n");
                report.append(String.format("TỔNG CỘNG: %,.0f VND\n", totalRevenue));
                report.append("=".repeat(50)).append("\n");
            }

            report.append("\nGHI CHÚ:\n");
            report.append("- Doanh thu được tính từ tất cả đơn hàng đã bán\n");
            report.append("- Dữ liệu cập nhật tự động từ database\n");

            textAreaBaoCao.setText(report.toString());
            textAreaBaoCao.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
            
        } catch (Exception e) {
            textAreaBaoCao.setText("LỖI KHI TẢI BÁO CÁO:\n" + e.getMessage());
        }
    }
}
