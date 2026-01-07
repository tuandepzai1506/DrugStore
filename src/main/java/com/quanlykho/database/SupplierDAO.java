package com.quanlykho.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class SupplierDAO {

    /**
     * Loại để lưu trữ thông tin nhà cung cấp
     */
    public static class Supplier {
        public int id;
        public String name;
        public String phone;
        public String address;

        public Supplier(int id, String name, String phone, String address) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.address = address;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Lấy tất cả nhà cung cấp
     */
    public static ObservableList<Supplier> getAllSuppliers() {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        String query = "SELECT id, name, phone, address FROM supplier ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }

        return suppliers;
    }

    /**
     * Lấy nhà cung cấp theo ID
     */
    public static Supplier getSupplierById(int id) {
        String query = "SELECT id, name, phone, address FROM supplier WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Thêm nhà cung cấp mới
     */
    public static boolean addSupplier(String name, String phone, String address) {
        String query = "INSERT INTO supplier (name, phone, address) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật thông tin nhà cung cấp
     */
    public static boolean updateSupplier(int id, String name, String phone, String address) {
        String query = "UPDATE supplier SET name = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setInt(4, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa nhà cung cấp
     */
    public static boolean deleteSupplier(int id) {
        String query = "DELETE FROM supplier WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
