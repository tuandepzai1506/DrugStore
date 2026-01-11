package com.quanlykho.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // Thay đổi HOST thành địa chỉ IP của máy tính khác
    // Ví dụ: "192.168.1.100" hoặc "192.168.0.5"
    private static final String HOST = "localhost";  // THAY ĐỔI ĐÂY
    private static final int PORT = 3306;
    private static final String DATABASE = "quanlykho";
    private static final String USERNAME = "root";  // Tên user MySQL trên máy khác
    private static final String PASSWORD = "";  // Mật khẩu MySQL
    
    private static Connection connection;
    
    public DatabaseConnection() {
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + 
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
        }
        return connection;
    }
    
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://" + HOST + ":" + PORT + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                USERNAME, PASSWORD)) {
            
            Statement stmt = conn.createStatement();
            
            // Create database if not exists
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE);
            System.out.println("Database created or already exists.");
            
            stmt.close();
        }
        
        // Now connect to the database and create tables
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Create Thuoc table
            String createThuocTable = "CREATE TABLE IF NOT EXISTS thuoc (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "ma_thuoc VARCHAR(50) UNIQUE NOT NULL," +
                    "ten_thuoc VARCHAR(255) NOT NULL," +
                    "don_vi_tinh VARCHAR(50)," +
                    "gia_nhap DECIMAL(10, 2)," +
                    "gia_ban DECIMAL(10, 2)," +
                    "so_luong INT DEFAULT 0," +
                    "han_su_dung DATE," +
                    "ghi_chu TEXT," +
                    "ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            
            stmt.execute(createThuocTable);
            System.out.println("Table 'thuoc' created or already exists.");
            
            stmt.close();
        }
    }
}
