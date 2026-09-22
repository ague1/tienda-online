package com.example.myapplication.features.product.domain.model;

public class Promotion {

    public Promotion() {
    }

    private String id;
    private String productId;
    private long specialPrice;
    private boolean active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(long specialPrice) {
        this.specialPrice = specialPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
