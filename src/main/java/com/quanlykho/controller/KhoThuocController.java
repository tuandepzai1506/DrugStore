package com.quanlykho.controller;

import com.quanlykho.database.MedicineDAO;
import com.quanlykho.database.StockInDAO;
import com.quanlykho.database.StockOutDAO;
import com.quanlykho.model.Thuoc;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class KhoThuocController implements Initializable {

    @FXML
    private TableView<Thuoc> tableThuoc;

    @FXML
    private TableColumn<Thuoc, String> colTen;

    @FXML
    private TableColumn<Thuoc, Integer> colSoLuong;

    @FXML
    private TableColumn<Thuoc, Double> colGia;

    @FXML
    private TableColumn<Thuoc, String> colHSD;

    @FXML
    private TableColumn<Thuoc, String> colTrangThai;

    @FXML
    private TextField txtSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // LOGIC: Giai đoạn khởi tạo
        // 1. Cấu hình các cột TableView
        // 2. Tải dữ liệu từ database
        // 3. Thiết lập listener cho tìm kiếm
        
        setupTableColumns();
        loadData();
        setupSearchListener();
    }

    /**
     * LOGIC: Cấu hình các cột của TableView
     * - Sử dụng PropertyValueFactory để binding dữ liệu từ model
     * - Cột trạng thái: tính toán động dựa trên số lượng tồn và hạn SD
     */
    private void setupTableColumns() {
        // Binding các cột tiêu chuẩn với property của Thuoc model
        colTen.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Cột giá: hiển thị với định dạng VND
        colGia.setCellValueFactory(new PropertyValueFactory<>("price"));
        colGia.setCellFactory(column -> new TableCell<Thuoc, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VND", price));
                }
            }
        });
        
        colHSD.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        // Cột số lượng tồn: lấy từ database (nhập - xuất)
        colSoLuong.setCellValueFactory(cellData -> {
            Thuoc thuoc = cellData.getValue();
            int soLuongNhap = StockInDAO.getTotalStockInByMedicineId(thuoc.getId());
            int soLuongXuat = StockOutDAO.getTotalStockOutByMedicineId(thuoc.getId());
            int soLuongTon = soLuongNhap - soLuongXuat;
            return new SimpleIntegerProperty(soLuongTon).asObject();
        });

        // Cột trạng thái: tính toán dựa trên 2 tiêu chí
        // - Hạn sử dụng: nếu < hôm nay = "Hết hạn"
        // - Số lượng: nếu = 0 = "Hết", nếu < 10 = "Sắp hết", còn lại = "OK"
        colTrangThai.setCellValueFactory(cellData -> {
            Thuoc thuoc = cellData.getValue();
            String trangThai = getTrangThai(thuoc);
            return new SimpleStringProperty(trangThai);
        });
    }

    /**
     * LOGIC: Tải dữ liệu từ database
     * - Gọi MedicineDAO.getAllMedicines() để lấy danh sách
     * - Thiết lập vào TableView
     * - Log số lượng để kiểm chứng
     */
    private void loadData() {
        try {
            // Lấy tất cả thuốc từ database
            var medicineList = MedicineDAO.getAllMedicines();
            tableThuoc.setItems(medicineList);
            System.out.println("Đã tải " + medicineList.size() + " loại thuốc");
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu", "Không thể tải danh sách thuốc từ database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * LOGIC: Xác định trạng thái thuốc
     * Kiểm tra 2 điều kiện:
     * 1. Hạn sử dụng: nếu <= hôm nay thì "Hết hạn" (đỏ)
     * 2. Số lượng tồn:
     *    - = 0: "Hết hàng" (đỏ)
     *    - 0 < số lượng < 10: "Sắp hết" (vàng)
     *    - >= 10: "Sẵn sàng" (xanh)
     */
    private String getTrangThai(Thuoc thuoc) {
        try {
            // Kiểm tra hạn sử dụng
            LocalDate hanSD = LocalDate.parse(thuoc.getExpiryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (hanSD.isBefore(LocalDate.now())) {
                return "Hết hạn";
            }
        } catch (Exception e) {
            System.err.println("Lỗi parse ngày: " + e.getMessage());
        }

        // Kiểm tra số lượng tồn
        int soLuongNhap = StockInDAO.getTotalStockInByMedicineId(thuoc.getId());
        int soLuongXuat = StockOutDAO.getTotalStockOutByMedicineId(thuoc.getId());
        int soLuongTon = soLuongNhap - soLuongXuat;

        if (soLuongTon == 0) {
            return "Hết hàng";
        } else if (soLuongTon < 10) {
            return "Sắp hết";
        } else {
            return "Sẵn sàng";
        }
    }

    /**
     * LOGIC: Thiết lập listener cho tìm kiếm
     * - Sử dụng textProperty().addListener() để lắng nghe thay đổi text
     * - Khi text rỗng: load lại tất cả dữ liệu
     * - Khi có text: gọi MedicineDAO.searchMedicineByName() để tìm
     * - Tìm kiếm tự động (real-time) không cần click button
     */
    private void setupSearchListener() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                // Nếu xóa hết text, load lại tất cả
                loadData();
            } else {
                // Nếu có text, tìm kiếm
                try {
                    var resultList = MedicineDAO.searchMedicineByName(newVal);
                    tableThuoc.setItems(resultList);
                    System.out.println("Tìm thấy " + resultList.size() + " kết quả");
                } catch (Exception e) {
                    showError("Lỗi tìm kiếm", "Không thể tìm kiếm: " + e.getMessage());
                }
            }
        });
    }

    /**
     * LOGIC: Hiển thị dialog lỗi cho người dùng
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
