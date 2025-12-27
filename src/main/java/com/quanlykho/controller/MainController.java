package com.quanlykho.controller;

import com.quanlykho.model.Thuoc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Thuoc> tableThuoc;

    @FXML
    private TableColumn<Thuoc, String> colTen;

    @FXML
    private TableColumn<Thuoc, Integer> colSoLuong;

    @FXML
    private TableColumn<Thuoc, String> colHanSD;

    private final ObservableList<Thuoc> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colHanSD.setCellValueFactory(new PropertyValueFactory<>("hanSuDung"));

        // Dữ liệu mẫu
        data.add(new Thuoc("Paracetamol", 50, "12/2026"));
        data.add(new Thuoc("Amoxicillin", 30, "08/2025"));

        tableThuoc.setItems(data);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        System.out.println("Tìm kiếm: " + keyword);
    }
}
