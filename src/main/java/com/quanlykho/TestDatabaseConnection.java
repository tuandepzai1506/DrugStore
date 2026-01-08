package com.quanlykho;

import com.quanlykho.database.DatabaseConnection;
import com.quanlykho.database.MedicineDAO;
import com.quanlykho.model.Thuoc;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        System.out.println("=== TEST DATABASE CONNECTION ===");
        
        // Test 1: Kết nối database
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Kết nối database thành công!");
                DatabaseConnection.closeConnection(conn);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // Test 2: Lấy dữ liệu từ database
        System.out.println("\n=== Danh sách thuốc từ database ===");
        try {
            ObservableList<Thuoc> medicines = MedicineDAO.getAllMedicines();
            if (medicines.isEmpty()) {
                System.out.println("⚠️  Không có dữ liệu thuốc trong database!");
            } else {
                System.out.println("✅ Lấy được " + medicines.size() + " thuốc:");
                medicines.forEach(t -> System.out.println("  - " + t.getName() + " (" + t.getPrice() + "đ)"));
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi lấy dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
