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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    @FXML
    private ComboBox<String> cbHangThuoc;

    @FXML
    private ComboBox<String> cbSapXepGia;

    @FXML
    private Button btnLocDuLieu;

    @FXML
    private Button btnResetBoDieuKien;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // LOGIC: Giai đoạn khởi tạo
        // 1. Cấu hình các cột TableView
        // 2. Tải dữ liệu từ database
        // 3. Thiết lập listener cho tìm kiếm
        // 4. Cấu hình ComboBox và button lọc
        
        setupTableColumns();
        loadData();
        setupSearchListener();
        setupFilterControls();
    }

    /**
     * LOGIC: Cấu hình các cột của TableView
     * - Sử dụng PropertyValueFactory để binding dữ liệu từ model
     * - Cột trạng thái: tính toán động dựa trên số lượng tồn và hạn SD
     * - Thiết lập custom row factory để highlight thuốc sắp hết hạn (< 30 ngày)
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
        
        // Thiết lập custom row factory để highlight dòng sắp hết hạn
        tableThuoc.setRowFactory(tv -> new TableRow<Thuoc>() {
            @Override
            protected void updateItem(Thuoc thuoc, boolean empty) {
                super.updateItem(thuoc, empty);
                
                if (empty || thuoc == null) {
                    setStyle("");
                } else {
                    // Kiểm tra nếu thuốc sắp hết hạn (< 30 ngày)
                    try {
                        LocalDate hanSD = LocalDate.parse(thuoc.getExpiryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalDate hienTai = LocalDate.now();
                        LocalDate sau30Ngay = hienTai.plusDays(30);
                        
                        // Nếu hạn SD nằm giữa hôm nay và sau 30 ngày, bôi màu đỏ nhạt
                        if (hanSD.isAfter(hienTai) && hanSD.isBefore(sau30Ngay) || hanSD.isEqual(sau30Ngay)) {
                            setStyle("-fx-background-color: #ffcccc;");
                        } else if (hanSD.isBefore(hienTai) || hanSD.isEqual(hienTai)) {
                            // Nếu đã hết hạn, bôi màu đỏ đậm
                            setStyle("-fx-background-color: #ff6666;");
                        } else {
                            setStyle("");
                        }
                    } catch (Exception e) {
                        setStyle("");
                    }
                }
            }
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
            
            // Nếu database rỗng, dùng dữ liệu mẫu
            if (medicineList == null || medicineList.isEmpty()) {
                medicineList = FXCollections.observableArrayList(
                    new Thuoc("Paracetamol 500mg", 50, "2026-12-31", 15000, "Công ty A"),
                    new Thuoc("Amoxicillin 250mg", 30, "2026-08-15", 20000, "Công ty B"),
                    new Thuoc("Ibuprofen 400mg", 45, "2027-01-20", 25000, "Công ty A"),
                    new Thuoc("Vitamin C 1000mg", 80, "2026-11-10", 10000, "Công ty C"),
                    new Thuoc("Aspirin 100mg", 60, "2027-03-05", 12000, "Công ty B"),
                    new Thuoc("Omeprazole 20mg", 35, "2026-09-30", 18000, "Công ty A"),
                    new Thuoc("Metformin 500mg", 40, "2027-05-15", 22000, "Công ty C"),
                    new Thuoc("Atorvastatin 10mg", 25, "2026-10-12", 35000, "Công ty B")
                );
                System.out.println("Dùng dữ liệu mẫu vì database rỗng");
            }
            
            tableThuoc.setItems(medicineList);
            System.out.println("Đã tải " + medicineList.size() + " loại thuốc");
        } catch (Exception e) {
            // Nếu lỗi database, dùng dữ liệu mẫu
            var medicineList = FXCollections.observableArrayList(
                new Thuoc("Paracetamol 500mg", 50, "2026-12-31", 15000, "Công ty A"),
                new Thuoc("Amoxicillin 250mg", 30, "2026-08-15", 20000, "Công ty B"),
                new Thuoc("Ibuprofen 400mg", 45, "2027-01-20", 25000, "Công ty A"),
                new Thuoc("Vitamin C 1000mg", 80, "2026-11-10", 10000, "Công ty C"),
                new Thuoc("Aspirin 100mg", 60, "2027-03-05", 12000, "Công ty B"),
                new Thuoc("Omeprazole 20mg", 35, "2026-09-30", 18000, "Công ty A"),
                new Thuoc("Metformin 500mg", 40, "2027-05-15", 22000, "Công ty C"),
                new Thuoc("Atorvastatin 10mg", 25, "2026-10-12", 35000, "Công ty B")
            );
            tableThuoc.setItems(medicineList);
            System.out.println("Sử dụng dữ liệu mẫu do lỗi database: " + e.getMessage());
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
            applyFilters();
        });
    }

    /**
     * LOGIC: Cấu hình các ComboBox và button cho bộ lọc
     * - ComboBox hãng thuốc: hiển thị danh sách hãng từ dữ liệu
     * - ComboBox sắp xếp giá: thấp đến cao, cao đến thấp, không sắp xếp
     * - Button lọc: áp dụng các bộ lọc
     * - Button reset: xóa tất cả bộ lọc
     */
    private void setupFilterControls() {
        // Cấu hình ComboBox sắp xếp giá
        cbSapXepGia.setItems(FXCollections.observableArrayList(
            "Không sắp xếp",
            "Giá thấp đến cao",
            "Giá cao đến thấp"
        ));
        cbSapXepGia.setValue("Không sắp xếp");
        cbSapXepGia.setOnAction(event -> applyFilters());

        // Cấu hình ComboBox hãng thuốc
        cbHangThuoc.setValue("Tất cả hãng");
        cbHangThuoc.setOnAction(event -> applyFilters());

        // Cấu hình button lọc và reset
        btnLocDuLieu.setOnAction(event -> applyFilters());
        btnResetBoDieuKien.setOnAction(event -> resetFilters());

        // Load danh sách hãng
        updateHangThuocList();
    }

    /**
     * LOGIC: Cập nhật danh sách hãng từ dữ liệu hiện tại
     */
    private void updateHangThuocList() {
        try {
            var medicineList = MedicineDAO.getAllMedicines();
            ObservableList<String> hangList = medicineList.stream()
                .map(Thuoc::getHaCungCap)
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            
            hangList.add(0, "Tất cả hãng");
            cbHangThuoc.setItems(hangList);
            cbHangThuoc.setValue("Tất cả hãng");
        } catch (Exception e) {
            System.err.println("Lỗi tải danh sách hãng: " + e.getMessage());
        }
    }

    /**
     * LOGIC: Áp dụng tất cả bộ lọc
     * - Lọc theo tên (tìm kiếm)
     * - Lọc theo hãng thuốc
     * - Sắp xếp theo giá
     */
    private void applyFilters() {
        try {
            // Lấy tất cả dữ liệu từ database
            var allMedicines = MedicineDAO.getAllMedicines();
            
            // Nếu database rỗng, dùng dữ liệu mẫu
            if (allMedicines == null || allMedicines.isEmpty()) {
                allMedicines = FXCollections.observableArrayList(
                    new Thuoc("Paracetamol 500mg", 50, "2026-12-31", 15000, "Công ty A"),
                    new Thuoc("Amoxicillin 250mg", 30, "2026-08-15", 20000, "Công ty B"),
                    new Thuoc("Ibuprofen 400mg", 45, "2027-01-20", 25000, "Công ty A"),
                    new Thuoc("Vitamin C 1000mg", 80, "2026-11-10", 10000, "Công ty C"),
                    new Thuoc("Aspirin 100mg", 60, "2027-03-05", 12000, "Công ty B"),
                    new Thuoc("Omeprazole 20mg", 35, "2026-09-30", 18000, "Công ty A"),
                    new Thuoc("Metformin 500mg", 40, "2027-05-15", 22000, "Công ty C"),
                    new Thuoc("Atorvastatin 10mg", 25, "2026-10-12", 35000, "Công ty B")
                );
            }
            
            String tenThuoc = txtSearch.getText().trim().toLowerCase();
            String hangThuoc = cbHangThuoc.getValue();
            String sapXepGia = cbSapXepGia.getValue();
            
            // Áp dụng bộ lọc
            var filteredList = allMedicines.stream()
                .filter(t -> tenThuoc.isEmpty() || t.getTen().toLowerCase().contains(tenThuoc))
                .filter(t -> "Tất cả hãng".equals(hangThuoc) || t.getHaCungCap().equals(hangThuoc))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            
            // Sắp xếp theo giá
            if ("Giá thấp đến cao".equals(sapXepGia)) {
                filteredList.sort((t1, t2) -> Double.compare(t1.getGia(), t2.getGia()));
            } else if ("Giá cao đến thấp".equals(sapXepGia)) {
                filteredList.sort((t1, t2) -> Double.compare(t2.getGia(), t1.getGia()));
            }
            
            tableThuoc.setItems(filteredList);
            System.out.println("Tìm thấy " + filteredList.size() + " kết quả");
            
        } catch (Exception e) {
            showError("Lỗi lọc dữ liệu", "Không thể lọc dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * LOGIC: Reset tất cả bộ lọc
     * - Xóa text tìm kiếm
     * - Chọn "Tất cả hãng"
     * - Chọn "Không sắp xếp"
     * - Load lại tất cả dữ liệu
     */
    private void resetFilters() {
        txtSearch.clear();
        cbHangThuoc.setValue("Tất cả hãng");
        cbSapXepGia.setValue("Không sắp xếp");
        loadData();
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
