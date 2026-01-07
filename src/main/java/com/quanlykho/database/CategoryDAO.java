package com.quanlykho.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CategoryDAO {

    /**
     * Lấy tất cả loại thuốc
     */
    public static ObservableList<String> getAllCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT name FROM category ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách loại thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Lấy ID loại thuốc theo tên
     */
    public static int getCategoryIdByName(String categoryName) {
        String query = "SELECT id FROM category WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID loại thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Lấy tên loại thuốc theo ID
     */
    public static String getCategoryNameById(int id) {
        String query = "SELECT name FROM category WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tên loại thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Thêm loại thuốc mới
     */
    public static boolean addCategory(String categoryName) {
        String query = "INSERT INTO category (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, categoryName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm loại thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa loại thuốc
     */
    public static boolean deleteCategory(int id) {
        String query = "DELETE FROM category WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa loại thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
