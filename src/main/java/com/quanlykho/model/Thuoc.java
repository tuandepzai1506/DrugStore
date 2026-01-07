package com.quanlykho.model;

import javafx.beans.property.*;

public class Thuoc {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty image;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty description;
    private final SimpleStringProperty expiryDate;
    private final SimpleIntegerProperty categoryId;
    private final SimpleStringProperty createdAt;

    // Constructor mặc định
    public Thuoc() {
        this(0, "", "", 0.0, "", "", "", 0, "");
    }

    // Constructor đầy đủ
    public Thuoc(int id, String name, String image, double price, String brand, 
                 String description, String expiryDate, int categoryId, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.image = new SimpleStringProperty(image);
        this.price = new SimpleDoubleProperty(price);
        this.brand = new SimpleStringProperty(brand);
        this.description = new SimpleStringProperty(description);
        this.expiryDate = new SimpleStringProperty(expiryDate);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getImage() {
        return image.get();
    }

    public double getPrice() {
        return price.get();
    }

    public String getBrand() {
        return brand.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getExpiryDate() {
        return expiryDate.get();
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public String getCreatedAt() {
        return createdAt.get();
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setBrand(String brand) {
        this.brand.set(brand);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate.set(expiryDate);
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt.set(createdAt);
    }

    // Property accessors (để dùng với TableView)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty imageProperty() {
        return image;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public StringProperty brandProperty() {
        return brand;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty expiryDateProperty() {
        return expiryDate;
    }

    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }

    public StringProperty createdAtProperty() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Thuoc{" +
                "id=" + id.get() +
                ", name='" + name.get() + '\'' +
                ", price=" + price.get() +
                ", brand='" + brand.get() + '\'' +
                ", expiryDate='" + expiryDate.get() + '\'' +
                ", categoryId=" + categoryId.get() +
                '}';
    }
}
