package com.quanlykho.controller;

import com.quanlykho.model.Thuoc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class BanThuocController {

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
    private Button btnTimKiem;
    @FXML
    private Button btnResetTimKiem;
    
    @FXML
    private ComboBox<String> cbThuoc;
    @FXML
    private TextField txtSoLuongBan;
    @FXML
    private Button btnThemVaoHoaDon;
    
    @FXML
    private TableView<String> tableGioHang;
    @FXML
    private TableColumn<String, String> colTenThuoc;
    @FXML
    private TableColumn<String, String> colSL;
    @FXML
    private TableColumn<String, String> colGia;
    @FXML
    private TableColumn<String, String> colThanhTien;
    @FXML
    private Label lblTongTien;
    @FXML
    private Button btnThanhToan;

    private ObservableList<Thuoc> danhSachThuocGoc;
    private ObservableList<Thuoc> danhSachThuocLoc;

    @FXML
    public void initialize() {
        // Dữ liệu mẫu
        danhSachThuocGoc = FXCollections.observableArrayList(
            new Thuoc("Paracetamol 500mg", 50, "2026-12-31", 15000, "Công ty A"),
            new Thuoc("Amoxicillin 250mg", 30, "2026-08-15", 20000, "Công ty B"),
            new Thuoc("Ibuprofen 400mg", 45, "2027-01-20", 25000, "Công ty A"),
            new Thuoc("Vitamin C 1000mg", 80, "2026-11-10", 10000, "Công ty C"),
            new Thuoc("Aspirin 100mg", 60, "2027-03-05", 12000, "Công ty B"),
            new Thuoc("Omeprazole 20mg", 35, "2026-09-30", 18000, "Công ty A"),
            new Thuoc("Metformin 500mg", 40, "2027-05-15", 22000, "Công ty C"),
            new Thuoc("Atorvastatin 10mg", 25, "2026-10-12", 35000, "Công ty B")
        );
        
        danhSachThuocLoc = FXCollections.observableArrayList(danhSachThuocGoc);
        
        // Cập nhật danh sách hãng
        ObservableList<String> haCungCapList = FXCollections.observableArrayList(
            danhSachThuocGoc.stream()
                .map(Thuoc::getHaCungCap)
                .distinct()
                .collect(Collectors.toList())
        );
        cbHaCungCap.setItems(haCungCapList);
        cbHaCungCap.getItems().add(0, "Tất cả");
        cbHaCungCap.setValue("Tất cả");
        
        // Cập nhật danh sách thuốc trong ComboBox
        updateCbThuoc();
        
        // Xử lý sự kiện tìm kiếm
        btnTimKiem.setOnAction(event -> timKiemVaLoc());
        btnResetTimKiem.setOnAction(event -> resetTimKiem());
        
        cbSapXepGia.setValue("Không sắp xếp");
    }

    private void timKiemVaLoc() {
        String tenThuoc = txtTenThuoc.getText().trim().toLowerCase();
        String haCungCap = cbHaCungCap.getValue();
        String sapXepGia = cbSapXepGia.getValue();
        
        double giaMin = 0;
        double giaMax = Double.MAX_VALUE;
        
        try {
            if (!txtGiaMin.getText().trim().isEmpty()) {
                giaMin = Double.parseDouble(txtGiaMin.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Giá tối thiểu phải là một số");
            return;
        }
        
        try {
            if (!txtGiaMax.getText().trim().isEmpty()) {
                giaMax = Double.parseDouble(txtGiaMax.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Giá tối đa phải là một số");
            return;
        }
        
        // Lọc dữ liệu
        final double finalGiaMin = giaMin;
        final double finalGiaMax = giaMax;
        danhSachThuocLoc = danhSachThuocGoc.stream()
            .filter(t -> tenThuoc.isEmpty() || t.getTen().toLowerCase().contains(tenThuoc))
            .filter(t -> "Tất cả".equals(haCungCap) || t.getHaCungCap().equals(haCungCap))
            .filter(t -> t.getGia() >= finalGiaMin && t.getGia() <= finalGiaMax)
            .collect(Collectors.toCollection(FXCollections::observableArrayList));
        
        // Sắp xếp giá
        if ("Thấp đến cao".equals(sapXepGia)) {
            danhSachThuocLoc.sort((t1, t2) -> Double.compare(t1.getGia(), t2.getGia()));
        } else if ("Cao đến thấp".equals(sapXepGia)) {
            danhSachThuocLoc.sort((t1, t2) -> Double.compare(t2.getGia(), t1.getGia()));
        }
        
        updateCbThuoc();
    }

    private void resetTimKiem() {
        txtTenThuoc.clear();
        cbHaCungCap.setValue("Tất cả");
        txtGiaMin.clear();
        txtGiaMax.clear();
        cbSapXepGia.setValue("Không sắp xếp");
        
        danhSachThuocLoc = FXCollections.observableArrayList(danhSachThuocGoc);
        updateCbThuoc();
    }

    private void updateCbThuoc() {
        ObservableList<String> thuocNames = FXCollections.observableArrayList(
            danhSachThuocLoc.stream()
                .map(t -> t.getTen() + " (Giá: " + formatPrice(t.getGia()) + ", Hãng: " + t.getHaCungCap() + ")")
                .collect(Collectors.toList())
        );
        cbThuoc.setItems(thuocNames);
    }

    private String formatPrice(double price) {
        return String.format("%.0f VNĐ", price);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
