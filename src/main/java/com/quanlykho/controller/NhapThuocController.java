package com.quanlykho.controller;

import com.quanlykho.model.Thuoc;
import com.quanlykho.database.StockInDAO;
import com.quanlykho.database.MedicineDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

            // Parse hạn sử dụng từ format dd/mm/yyyy sang yyyy-MM-dd
            LocalDate expiryDate = LocalDate.now().plusMonths(12); // Mặc định 12 tháng nếu không nhập
            if (!hanSD.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    expiryDate = LocalDate.parse(hanSD, formatter);
                } catch (Exception e) {
                    showWarning("Cảnh báo", "Định dạng ngày không đúng, vui lòng nhập dd/mm/yyyy");
                    return;
                }
            }

            // Tạo object Thuoc (lưu số lượng vào id tạm thời)
            Thuoc thuoc = new Thuoc(
                soLuong,  // Sử dụng id để lưu số lượng tạm
                tenThuoc,
                "",
                giaNhap,
                "",
                "",
                expiryDate.toString(),  // Lưu định dạng chuẩn yyyy-MM-dd
                1,  // category_id mặc định là 1
                ""
            );

            // Thêm vào danh sách tạm
            nhapList.add(thuoc);
            System.out.println("✓ Đã thêm " + tenThuoc + " x" + soLuong + " vào danh sách nhập kho");

            // Xóa form để nhập tiếp
            clearForm();
            
            // Cập nhật TableView tự động (vì dùng ObservableList)
            tablePhieuNhap.refresh();

        } catch (NumberFormatException e) {
            showError("Lỗi", "Giá và số lượng phải là số");
        } catch (Exception e) {
            showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
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

    /**
     * LOGIC: Hoàn thành nhập kho - Lưu tất cả dữ liệu vào database
     * 1. Kiểm tra danh sách nhập có dữ liệu không
     * 2. Lưu từng thuốc vào bảng medicine (nếu chưa tồn tại)
     * 3. Lưu phiếu nhập vào bảng stock_in
     * 4. Xóa danh sách tạm và reset form
     * 5. Thông báo thành công cho user
     */
    @FXML
    private void handleFinishImport() {
        try {
            if (nhapList.isEmpty()) {
                showWarning("Cảnh báo", "Danh sách nhập kho trống, vui lòng thêm thuốc trước");
                return;
            }

            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (Thuoc thuoc : nhapList) {
                try {
                    String tenThuoc = thuoc.getName();
                    int quantity = thuoc.getId(); // ID tạm dùng làm số lượng
                    
                    System.out.println("\n--- Xử lý thuốc: " + tenThuoc + " x" + quantity + " ---");

                    // Bước 1: Kiểm tra và thêm thuốc nếu chưa tồn tại
                    int medicineId = getMedicineIdByName(tenThuoc);
                    System.out.println("Tìm thuốc: " + tenThuoc + " -> ID: " + medicineId);
                    
                    if (medicineId <= 0) {
                        // Thêm thuốc mới vào medicine table
                        System.out.println("Thuốc chưa tồn tại, thêm mới...");
                        if (MedicineDAO.addMedicine(thuoc)) {
                            // Tìm lại ID sau khi thêm
                            Thread.sleep(100); // Chờ để đảm bảo dữ liệu đã lưu
                            medicineId = getMedicineIdByName(tenThuoc);
                            System.out.println("✓ Thêm thuốc mới thành công: " + tenThuoc + " -> ID: " + medicineId);
                        } else {
                            String error = "Thêm thuốc mới thất bại: " + tenThuoc;
                            System.err.println("✗ " + error);
                            errorMessages.append("\n- ").append(error);
                            failCount++;
                            continue;
                        }
                    }

                    if (medicineId <= 0) {
                        String error = "Không tìm thấy ID thuốc sau khi thêm: " + tenThuoc;
                        System.err.println("✗ " + error);
                        errorMessages.append("\n- ").append(error);
                        failCount++;
                        continue;
                    }

                    // Bước 2: Lưu phiếu nhập kho
                    LocalDate dateIn = LocalDate.now();
                    System.out.println("Lưu phiếu nhập: medicineId=" + medicineId + ", quantity=" + quantity + ", dateIn=" + dateIn);

                    if (StockInDAO.addStockIn(medicineId, 1, quantity, dateIn)) {
                        System.out.println("✓ Nhập kho thành công: " + tenThuoc + " x" + quantity);
                        successCount++;
                    } else {
                        String error = "Nhập kho thất bại: " + tenThuoc;
                        System.err.println("✗ " + error);
                        errorMessages.append("\n- ").append(error);
                        failCount++;
                    }

                } catch (Exception e) {
                    String error = "Lỗi xử lý thuốc " + thuoc.getName() + ": " + e.getMessage();
                    System.err.println("✗ " + error);
                    e.printStackTrace();
                    errorMessages.append("\n- ").append(error);
                    failCount++;
                }
            }

            // Thông báo kết quả
            String message = "Thành công: " + successCount + " thuốc\nThất bại: " + failCount + " thuốc";
            if (failCount > 0 && errorMessages.length() > 0) {
                message += "\n\nChi tiết lỗi:" + errorMessages.toString();
            }
            
            System.out.println("\n=== KẾT QUẢ NHẬP KHO ===");
            System.out.println(message);
            
            showInfo("Hoàn thành nhập kho", message);

            // Reset form
            nhapList.clear();
            tablePhieuNhap.refresh();
            clearForm();

        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    /**
     * LOGIC: Lấy ID thuốc theo tên (tìm kiếm trong database)
     */
    private int getMedicineIdByName(String name) {
        Thuoc thuoc = MedicineDAO.searchMedicineByName(name).stream()
            .filter(t -> t.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
        return thuoc != null ? thuoc.getId() : -1;
    }

    /**
     * LOGIC: Hiển thị dialog thông tin
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
