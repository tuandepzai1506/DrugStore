package com.quanlykho.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class StockOutDAO {

    /**
     * Loại để lưu trữ thông tin xuất kho
     */
    public static class StockOut {
        public int id;
        public int medicineId;
        public String medicineName;
        public int quantity;
        public LocalDate dateOut;
        public double price;

        public StockOut(int id, int medicineId, String medicineName, int quantity, LocalDate dateOut, double price) {
            this.id = id;
            this.medicineId = medicineId;
            this.medicineName = medicineName;
            this.quantity = quantity;
            this.dateOut = dateOut;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Xuất: " + medicineName + " x" + quantity + " ngày " + dateOut;
        }
    }

    /**
     * Lấy tất cả phiếu xuất kho
     */
    public static ObservableList<StockOut> getAllStockOut() {
        ObservableList<StockOut> stockOutList = FXCollections.observableArrayList();
        String query = "SELECT so.id, so.medicine_id, m.name, so.quantity, so.date_out, m.price " +
                      "FROM stock_out so " +
                      "JOIN medicine m ON so.medicine_id = m.id " +
                      "ORDER BY so.date_out DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                StockOut stockOut = new StockOut(
                    rs.getInt("id"),
                    rs.getInt("medicine_id"),
                    rs.getString("m.name"),
                    rs.getInt("quantity"),
                    rs.getDate("date_out").toLocalDate(),
                    rs.getDouble("price")
                );
                stockOutList.add(stockOut);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách xuất kho: " + e.getMessage());
            e.printStackTrace();
        }

        return stockOutList;
    }

    /**
     * Thêm phiếu xuất kho
     */
    public static boolean addStockOut(int medicineId, int quantity, LocalDate dateOut) {
        String query = "INSERT INTO stock_out (medicine_id, quantity, date_out) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, medicineId);
            pstmt.setInt(2, quantity);
            pstmt.setDate(3, java.sql.Date.valueOf(dateOut));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm phiếu xuất kho: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy tổng số lượng xuất của một thuốc
     */
    public static int getTotalStockOutByMedicineId(int medicineId) {
        String query = "SELECT IFNULL(SUM(quantity), 0) as total FROM stock_out WHERE medicine_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, medicineId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tổng xuất kho: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Xóa phiếu xuất kho
     */
    public static boolean deleteStockOut(int id) {
        String query = "DELETE FROM stock_out WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa phiếu xuất kho: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy doanh thu theo ngày (sử dụng view)
     */
    public static ObservableList<String> getDailyRevenue() {
        ObservableList<String> revenues = FXCollections.observableArrayList();
        String query = "SELECT * FROM view_daily_revenue ORDER BY date_out DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String revenueRecord = rs.getDate("date_out") + ": " + 
                                      String.format("%,.0f VND", rs.getDouble("revenue"));
                revenues.add(revenueRecord);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy doanh thu theo ngày: " + e.getMessage());
            e.printStackTrace();
        }

        return revenues;
    }
}
