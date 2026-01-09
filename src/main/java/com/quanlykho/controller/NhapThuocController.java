package com.quanlykho.controller;

import com.quanlykho.model.Thuoc;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.ResourceBundle;

public class NhapThuocController implements Initializable {

    @FXML
    private TextField txtTenThuoc;

    @FXML
    private TextField txtSoLuong;

    @FXML
    private TextField txtGiaNhap;

    @FXML
    private TextField txtHanSuDung;

    @FXML
    private TableView<Thuoc> tablePhieuNhap;

    private ObservableList<Thuoc> nhapList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // LOGIC: Khởi tạo màn hình nhập kho
        // 1. Cấu hình TableView
        // 2. Chuẩn bị danh sách tạm (chưa lưu vào DB)
        
        setupTableView();
    }

    /**
     * LOGIC: Cấu hình TableView hiển thị thuốc vừa nhập
     * - Liên kết các cột với property của model Thuoc
     * - Danh sách được lưu tạm trong nhapList (chưa commit DB)
     */
    @SuppressWarnings("unchecked")
    private void setupTableView() {
        if (tablePhieuNhap != null && tablePhieuNhap.getColumns().size() > 0) {
            tablePhieuNhap.setItems(nhapList);
            
            // Binding các cột (giả sử FXML đã định nghĩa fx:id)
            TableColumn<Thuoc, String> col0 = (TableColumn<Thuoc, String>) tablePhieuNhap.getColumns().get(0);
            TableColumn<Thuoc, Integer> col1 = (TableColumn<Thuoc, Integer>) tablePhieuNhap.getColumns().get(1);
            TableColumn<Thuoc, Double> col2 = (TableColumn<Thuoc, Double>) tablePhieuNhap.getColumns().get(2);
            TableColumn<Thuoc, String> col3 = (TableColumn<Thuoc, String>) tablePhieuNhap.getColumns().get(3);
            
            col0.setCellValueFactory(new PropertyValueFactory<>("name"));
            col1.setCellValueFactory(new PropertyValueFactory<>("id")); // Tạm dùng id làm SL
            col2.setCellValueFactory(new PropertyValueFactory<>("price"));
            col3.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        }
    }

    /**
     * LOGIC: Xử lý khi click "Lưu thông tin"
     * 1. Kiểm tra dữ liệu nhập (validate)
     * 2. Tạo object Thuoc từ dữ liệu form
     * 3. Thêm vào danh sách tạm (nhapList)
     * 4. Xóa form để nhập tiếp thuốc khác
     * 5. Khi click "Hoàn thành lô nhập" mới save vào DB
     */
    @FXML
    private void handleSave() {
        try {
            // VALIDATION: Kiểm tra dữ liệu
            if (txtTenThuoc.getText().trim().isEmpty()) {
                showWarning("Cảnh báo", "Vui lòng nhập tên thuốc");
                return;
            }

            if (txtSoLuong.getText().trim().isEmpty()) {
                showWarning("Cảnh báo", "Vui lòng nhập số lượng");
                return;
            }

            int soLuong = Integer.parseInt(txtSoLuong.getText());
            if (soLuong <= 0) {
                showWarning("Cảnh báo", "Số lượng phải lớn hơn 0");
                return;
            }

            // Lấy thông tin từ form
            String tenThuoc = txtTenThuoc.getText().trim();
            double giaNhap = Double.parseDouble(txtGiaNhap.getText().isEmpty() ? "0" : txtGiaNhap.getText());
            String hanSD = txtHanSuDung.getText().trim();

            // Tạo object Thuoc (tạm thời, chưa save DB)
            Thuoc thuoc = new Thuoc(
                0, // ID tạm (sẽ lấy từ DB sau)
                tenThuoc,
                "",
                giaNhap,
                "",
                "",
                hanSD,
                0,
                ""
            );

            // Thêm vào danh sách tạm
            nhapList.add(thuoc);
            System.out.println("✓ Đã thêm " + tenThuoc + " vào danh sách nhập kho");

            // Xóa form để nhập tiếp
            clearForm();
            
            // Cập nhật TableView tự động (vì dùng ObservableList)
            tablePhieuNhap.refresh();

        } catch (NumberFormatException e) {
            showError("Lỗi", "Giá và số lượng phải là số");
        } catch (Exception e) {
            showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    /**
     * LOGIC: Xóa form input để nhập thuốc mới
     */
    private void clearForm() {
        txtTenThuoc.clear();
        txtSoLuong.clear();
        txtGiaNhap.clear();
        txtHanSuDung.clear();
        txtTenThuoc.requestFocus();
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
