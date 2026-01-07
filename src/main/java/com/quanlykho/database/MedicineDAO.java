package com.quanlykho.database;

import com.quanlykho.model.Thuoc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MedicineDAO {

    /**
     * Lấy tất cả danh sách thuốc từ database
     */
    public static ObservableList<Thuoc> getAllMedicines() {
        ObservableList<Thuoc> medicineList = FXCollections.observableArrayList();
        String query = "SELECT id, name, image, price, brand, description, expiry_date, category_id, created_at FROM medicine ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Thuoc thuoc = new Thuoc(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("image"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("description"),
                    rs.getDate("expiry_date").toString(),
                    rs.getInt("category_id"),
                    rs.getTimestamp("created_at").toString()
                );
                medicineList.add(thuoc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return medicineList;
    }

    /**
     * Tìm kiếm thuốc theo tên
     */
    public static ObservableList<Thuoc> searchMedicineByName(String keyword) {
        ObservableList<Thuoc> medicineList = FXCollections.observableArrayList();
        String query = "SELECT id, name, image, price, brand, description, expiry_date, category_id, created_at FROM medicine WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Thuoc thuoc = new Thuoc(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("image"),
                        rs.getDouble("price"),
                        rs.getString("brand"),
                        rs.getString("description"),
                        rs.getDate("expiry_date").toString(),
                        rs.getInt("category_id"),
                        rs.getTimestamp("created_at").toString()
                    );
                    medicineList.add(thuoc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return medicineList;
    }

    /**
     * Lấy thuốc theo ID
     */
    public static Thuoc getMedicineById(int id) {
        String query = "SELECT id, name, image, price, brand, description, expiry_date, category_id, created_at FROM medicine WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Thuoc(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("image"),
                        rs.getDouble("price"),
                        rs.getString("brand"),
                        rs.getString("description"),
                        rs.getDate("expiry_date").toString(),
                        rs.getInt("category_id"),
                        rs.getTimestamp("created_at").toString()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy thuốc theo ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Thêm thuốc mới
     */
    public static boolean addMedicine(Thuoc thuoc) {
        String query = "INSERT INTO medicine (name, image, price, brand, description, expiry_date, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, thuoc.getName());
            pstmt.setString(2, thuoc.getImage());
            pstmt.setDouble(3, thuoc.getPrice());
            pstmt.setString(4, thuoc.getBrand());
            pstmt.setString(5, thuoc.getDescription());
            pstmt.setDate(6, java.sql.Date.valueOf(thuoc.getExpiryDate()));
            pstmt.setInt(7, thuoc.getCategoryId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật thông tin thuốc
     */
    public static boolean updateMedicine(Thuoc thuoc) {
        String query = "UPDATE medicine SET name = ?, image = ?, price = ?, brand = ?, description = ?, expiry_date = ?, category_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, thuoc.getName());
            pstmt.setString(2, thuoc.getImage());
            pstmt.setDouble(3, thuoc.getPrice());
            pstmt.setString(4, thuoc.getBrand());
            pstmt.setString(5, thuoc.getDescription());
            pstmt.setDate(6, java.sql.Date.valueOf(thuoc.getExpiryDate()));
            pstmt.setInt(7, thuoc.getCategoryId());
            pstmt.setInt(8, thuoc.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa thuốc theo ID
     */
    public static boolean deleteMedicine(int id) {
        String query = "DELETE FROM medicine WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy tất cả thuốc sắp hết hạn (trong 30 ngày)
     */
    public static ObservableList<Thuoc> getExpiringMedicines() {
        ObservableList<Thuoc> medicineList = FXCollections.observableArrayList();
        String query = "SELECT * FROM view_expiring_soon ORDER BY expiry_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Thuoc thuoc = new Thuoc(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("image"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("description"),
                    rs.getDate("expiry_date").toString(),
                    rs.getInt("category_id"),
                    rs.getTimestamp("created_at").toString()
                );
                medicineList.add(thuoc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy thuốc sắp hết hạn: " + e.getMessage());
            e.printStackTrace();
        }

        return medicineList;
    }
}
