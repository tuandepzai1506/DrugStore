package com.quanlykho.database;

import java.sql.*;

public class StatisticsDAO {

    /**
     * Lấy tổng số lượng hàng đã bán hôm nay
     */
    public static int getTodaysSaleQuantity() {
        String query = "SELECT IFNULL(SUM(quantity), 0) as total FROM stock_out WHERE DATE(date_out) = CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy doanh số hôm nay: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy doanh thu hôm nay (số lượng * giá)
     */
    public static double getTodaysRevenue() {
        String query = "SELECT IFNULL(SUM(so.quantity * m.price), 0) as revenue " +
                      "FROM stock_out so " +
                      "JOIN medicine m ON so.medicine_id = m.id " +
                      "WHERE DATE(so.date_out) = CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy doanh thu hôm nay: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy tổng số lượng hàng nhập hôm nay
     */
    public static int getTodaysImportQuantity() {
        String query = "SELECT IFNULL(SUM(quantity), 0) as total FROM stock_in WHERE DATE(date_in) = CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy nhập hôm nay: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy tổng giá trị hàng nhập hôm nay
     */
    public static double getTodaysImportValue() {
        String query = "SELECT IFNULL(SUM(si.quantity * m.price), 0) as value " +
                      "FROM stock_in si " +
                      "JOIN medicine m ON si.medicine_id = m.id " +
                      "WHERE DATE(si.date_in) = CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("value");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy giá trị nhập hôm nay: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy tổng số loại hàng trong kho
     */
    public static int getTotalMedicineTypes() {
        String query = "SELECT COUNT(*) as total FROM medicine";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tổng loại thuốc: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy tổng số lượng hàng tồn kho
     */
    public static int getTotalStockQuantity() {
        String query = "SELECT IFNULL(SUM(IFNULL(si.quantity, 0) - IFNULL(so.quantity, 0)), 0) as total " +
                      "FROM medicine m " +
                      "LEFT JOIN stock_in si ON m.id = si.medicine_id " +
                      "LEFT JOIN stock_out so ON m.id = so.medicine_id " +
                      "GROUP BY m.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int total = 0;
            while (rs.next()) {
                total += rs.getInt("total");
            }
            return total;
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tồn kho: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy giá trị tổng tồn kho (tồn * giá)
     */
    public static double getTotalStockValue() {
        String query = "SELECT IFNULL(SUM(stock_qty * price), 0) as value " +
                      "FROM (SELECT m.id, m.price, IFNULL(SUM(si.quantity), 0) - IFNULL(SUM(so.quantity), 0) as stock_qty " +
                      "FROM medicine m " +
                      "LEFT JOIN stock_in si ON m.id = si.medicine_id " +
                      "LEFT JOIN stock_out so ON m.id = so.medicine_id " +
                      "GROUP BY m.id, m.price) as inventory";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("value");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy giá trị tồn kho: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy số lượng hàng sắp hết (tồn < 10)
     */
    public static int getLowStockCount() {
        String query = "SELECT COUNT(*) as count " +
                      "FROM (" +
                      "  SELECT m.id, (IFNULL(SUM(si.quantity), 0) - IFNULL(SUM(so.quantity), 0)) as stock " +
                      "  FROM medicine m " +
                      "  LEFT JOIN stock_in si ON m.id = si.medicine_id " +
                      "  LEFT JOIN stock_out so ON m.id = so.medicine_id " +
                      "  GROUP BY m.id " +
                      "  HAVING stock < 10 AND stock > 0" +
                      ") as low_stock";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy số hàng sắp hết: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy số lượng hàng hết (tồn = 0)
     */
    public static int getOutOfStockCount() {
        String query = "SELECT COUNT(*) as count " +
                      "FROM (" +
                      "  SELECT m.id, (IFNULL(SUM(si.quantity), 0) - IFNULL(SUM(so.quantity), 0)) as stock " +
                      "  FROM medicine m " +
                      "  LEFT JOIN stock_in si ON m.id = si.medicine_id " +
                      "  LEFT JOIN stock_out so ON m.id = so.medicine_id " +
                      "  GROUP BY m.id " +
                      "  HAVING stock = 0" +
                      ") as out_of_stock";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy số hàng hết: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}
