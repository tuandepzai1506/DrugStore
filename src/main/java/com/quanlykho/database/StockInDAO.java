package com.quanlykho.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class StockInDAO {

    /**
     * Loại để lưu trữ thông tin nhập kho
     */
    public static class StockIn {
        public int id;
        public int medicineId;
        public String medicineName;
        public int supplierId;
        public String supplierName;
        public int quantity;
        public LocalDate dateIn;

        public StockIn(int id, int medicineId, String medicineName, int supplierId, 
                      String supplierName, int quantity, LocalDate dateIn) {
            this.id = id;
            this.medicineId = medicineId;
            this.medicineName = medicineName;
            this.supplierId = supplierId;
            this.supplierName = supplierName;
            this.quantity = quantity;
            this.dateIn = dateIn;
        }

        @Override
        public String toString() {
            return "Nhập: " + medicineName + " x" + quantity + " ngày " + dateIn;
        }
    }

    /**
     * Lấy tất cả phiếu nhập kho
     */
    public static ObservableList<StockIn> getAllStockIn() {
        ObservableList<StockIn> stockInList = FXCollections.observableArrayList();
        String query = "SELECT si.id, si.medicine_id, m.name, si.supplier_id, s.name, si.quantity, si.date_in " +
                      "FROM stock_in si " +
                      "JOIN medicine m ON si.medicine_id = m.id " +
                      "LEFT JOIN supplier s ON si.supplier_id = s.id " +
                      "ORDER BY si.date_in DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                StockIn stockIn = new StockIn(
                    rs.getInt("si.id"),
                    rs.getInt("medicine_id"),
                    rs.getString("m.name"),
                    rs.getInt("supplier_id"),
                    rs.getString("s.name"),
                    rs.getInt("quantity"),
                    rs.getDate("date_in").toLocalDate()
                );
                stockInList.add(stockIn);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách nhập kho: " + e.getMessage());
            e.printStackTrace();
        }

        return stockInList;
    }

    /**
     * Thêm phiếu nhập kho
     */
    public static boolean addStockIn(int medicineId, int supplierId, int quantity, LocalDate dateIn) {
        String query = "INSERT INTO stock_in (medicine_id, supplier_id, quantity, date_in) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, medicineId);
            pstmt.setInt(2, supplierId);
            pstmt.setInt(3, quantity);
            pstmt.setDate(4, java.sql.Date.valueOf(dateIn));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm phiếu nhập kho: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy tổng số lượng nhập của một thuốc
     */
    public static int getTotalStockInByMedicineId(int medicineId) {
        String query = "SELECT IFNULL(SUM(quantity), 0) as total FROM stock_in WHERE medicine_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, medicineId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tổng nhập kho: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Xóa phiếu nhập kho
     */
    public static boolean deleteStockIn(int id) {
        String query = "DELETE FROM stock_in WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa phiếu nhập kho: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
