package com.quanlykho.controller;

import com.quanlykho.database.MedicineDAO;
import com.quanlykho.database.StockInDAO;
import com.quanlykho.database.StockOutDAO;
import com.quanlykho.model.Thuoc;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BanThuocController implements Initializable {

    @FXML
    private TextField txtTenThuoc;

    @FXML
    private ComboBox<String> cbHaCungCap;

    @FXML
    private TextField txtGiaMin;

    @FXML
    private TextField txtGiaMax;

    @FXML
    private ComboBox<String> cbSapXepGia;

    @FXML
    private ComboBox<Thuoc> cbThuoc;

    @FXML
    private TextField txtSoLuongBan;

    @FXML
    private Button btnTimKiem;

    @FXML
    private Button btnResetTimKiem;

    @FXML
    private Button btnThemVaoHoaDon;

    private ObservableList<Thuoc> danhSachThuoc;
    private ObservableList<Thuoc> hoaDonList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // LOGIC: Khởi tạo màn hình bán hàng
        // 1. Tải danh sách thuốc từ database
        // 2. Thiết lập các dropdown
        // 3. Thiết lập event listeners
        
        loadMedicines();
        setupComboBoxes();
        setupEventListeners();
    }

    /**
     * LOGIC: Tải danh sách thuốc từ database
     * - Lấy tất cả thuốc từ MedicineDAO
     * - Lưu vào danhSachThuoc để dùng cho tìm kiếm/lọc
     */
    private void loadMedicines() {
        try {
            danhSachThuoc = MedicineDAO.getAllMedicines();
            cbThuoc.setItems(danhSachThuoc);
            System.out.println("✓ Đã tải " + danhSachThuoc.size() + " loại thuốc");
        } catch (Exception e) {
            showError("Lỗi", "Không thể tải danh sách thuốc: " + e.getMessage());
        }
    }

    /**
     * LOGIC: Cấu hình các ComboBox
     * - cbHaCungCap: lấy danh sách thương hiệu từ thuốc
     * - cbSapXepGia: đã cấu hình static trong FXML
     * - cbThuoc: sẽ được cập nhật khi tìm kiếm
     */
    private void setupComboBoxes() {
        // Lấy danh sách thương hiệu (brand) từ medicines
        if (danhSachThuoc != null && !danhSachThuoc.isEmpty()) {
            ObservableList<String> brands = FXCollections.observableArrayList(
                danhSachThuoc.stream()
                    .map(Thuoc::getBrand)
                    .distinct()
                    .filter(b -> b != null && !b.isEmpty())
                    .collect(Collectors.toList())
            );
            cbHaCungCap.setItems(brands);
        }
    }

    /**
     * LOGIC: Thiết lập event listeners
     * - btnTimKiem: gọi handleSearch()
     * - btnResetTimKiem: gọi handleResetSearch()
     * - btnThemVaoHoaDon: gọi handleAddToInvoice()
     */
    private void setupEventListeners() {
        btnTimKiem.setOnAction(event -> handleSearch());
        btnResetTimKiem.setOnAction(event -> handleResetSearch());
        btnThemVaoHoaDon.setOnAction(event -> handleAddToInvoice());
    }

    /**
     * LOGIC: Xử lý tìm kiếm thuốc
     * - Lọc theo tên: sử dụng MedicineDAO.searchMedicineByName()
     * - Lọc theo hãng: so sánh brand
     * - Lọc theo giá: so sánh price với range
     * - Sắp xếp giá: nếu chọn
     * - Cập nhật cbThuoc với kết quả
     */
    @FXML
    private void handleSearch() {
        try {
            ObservableList<Thuoc> filteredList = danhSachThuoc;

            // FILTER 1: Tên thuốc
            String tenThuoc = txtTenThuoc.getText().trim();
            if (!tenThuoc.isEmpty()) {
                filteredList = MedicineDAO.searchMedicineByName(tenThuoc);
            }

            // FILTER 2: Hãng cung cấp
            String selectedBrand = cbHaCungCap.getValue();
            if (selectedBrand != null && !selectedBrand.isEmpty()) {
                filteredList = FXCollections.observableArrayList(
                    filteredList.stream()
                        .filter(t -> t.getBrand() != null && t.getBrand().equals(selectedBrand))
                        .collect(Collectors.toList())
                );
            }

            // FILTER 3: Khoảng giá
            if (!txtGiaMin.getText().isEmpty() || !txtGiaMax.getText().isEmpty()) {
                double giaMin = txtGiaMin.getText().isEmpty() ? 0 : Double.parseDouble(txtGiaMin.getText());
                double giaMax = txtGiaMax.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtGiaMax.getText());
                
                filteredList = FXCollections.observableArrayList(
                    filteredList.stream()
                        .filter(t -> t.getPrice() >= giaMin && t.getPrice() <= giaMax)
                        .collect(Collectors.toList())
                );
            }

            // SORT: Sắp xếp giá
            String sapXep = cbSapXepGia.getValue();
            if (sapXep != null) {
                if (sapXep.equals("Thấp đến cao")) {
                    filteredList.sort((t1, t2) -> Double.compare(t1.getPrice(), t2.getPrice()));
                } else if (sapXep.equals("Cao đến thấp")) {
                    filteredList.sort((t1, t2) -> Double.compare(t2.getPrice(), t1.getPrice()));
                }
            }

            cbThuoc.setItems(filteredList);
            System.out.println("Tìm thấy " + filteredList.size() + " loại thuốc");
            
        } catch (NumberFormatException e) {
            showError("Lỗi", "Giá phải là số");
        } catch (Exception e) {
            showError("Lỗi", "Có lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    /**
     * LOGIC: Đặt lại tìm kiếm
     * - Xóa tất cả filter
     * - Load lại danh sách thuốc gốc
     */
    @FXML
    private void handleResetSearch() {
        txtTenThuoc.clear();
        cbHaCungCap.setValue(null);
        txtGiaMin.clear();
        txtGiaMax.clear();
        cbSapXepGia.setValue("Không sắp xếp");
        cbThuoc.setItems(danhSachThuoc);
        System.out.println("✓ Đã đặt lại bộ lọc");
    }

    /**
     * LOGIC: Thêm thuốc vào hóa đơn
     * 1. Lấy thuốc đã chọn từ cbThuoc
     * 2. Kiểm tra số lượng nhập (> 0)
     * 3. Kiểm tra tồn kho (có đủ không)
     * 4. Thêm vào hoaDonList (tạm thời)
     * 5. Chuẩn bị để save vào stock_out
     */
    @FXML
    private void handleAddToInvoice() {
        try {
            Thuoc thuoc = cbThuoc.getValue();
            if (thuoc == null) {
                showWarning("Cảnh báo", "Vui lòng chọn thuốc");
                return;
            }

            String soLuongText = txtSoLuongBan.getText().trim();
            if (soLuongText.isEmpty()) {
                showWarning("Cảnh báo", "Vui lòng nhập số lượng bán");
                return;
            }

            int soLuongBan = Integer.parseInt(soLuongText);
            if (soLuongBan <= 0) {
                showWarning("Cảnh báo", "Số lượng phải lớn hơn 0");
                return;
            }

            // Kiểm tra tồn kho
            int soLuongNhap = StockInDAO.getTotalStockInByMedicineId(thuoc.getId());
            int soLuongXuat = StockOutDAO.getTotalStockOutByMedicineId(thuoc.getId());
            int soLuongTon = soLuongNhap - soLuongXuat;

            if (soLuongBan > soLuongTon) {
                showError("Lỗi", "Không đủ tồn kho! Chỉ còn " + soLuongTon + " cái");
                return;
            }

            // Thêm vào hóa đơn tạm
            hoaDonList.add(thuoc);
            System.out.println("✓ Đã thêm " + thuoc.getName() + " x" + soLuongBan + " vào hóa đơn");

            // Reset form
            cbThuoc.setValue(null);
            txtSoLuongBan.clear();

        } catch (NumberFormatException e) {
            showError("Lỗi", "Số lượng phải là số");
        } catch (Exception e) {
            showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    /**
     * LOGIC: Hiển thị dialog cảnh báo
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * LOGIC: Hiển thị dialog lỗi
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
