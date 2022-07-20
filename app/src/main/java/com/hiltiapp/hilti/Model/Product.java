package com.hiltiapp.hilti.Model;

public class Product {

    public String  id,modelId, image, name, code, description, price, extraPrice, bulkPrice,totalPrice, model, brand, notes, quantity, category;

    public Product() {
    }

    public Product(String id, String modelId, String image, String name, String code, String description, String price, String extraPrice, String bulkPrice, String model, String brand, String notes) {
        this.id = id;
        this.modelId = modelId;
        this.image = image;
        this.name = name;
        this.code = code;
        this.description = description;
        this.price = price;
        this.extraPrice = extraPrice;
        this.bulkPrice = bulkPrice;
        this.model = model;
        this.brand = brand;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(String extraPrice) {
        this.extraPrice = extraPrice;
    }

    public String getModelId() {
        return modelId;
    }

    public String getBulkPrice() {
        return bulkPrice;
    }

    public void setBulkPrice(String bulkPrice) {
        this.bulkPrice = bulkPrice;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}