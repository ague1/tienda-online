package com.example.myapplication.features.cart.model;
public class CartItem {

    private String productId;
    private String nombre;
    private String image;
    private double precio;
    private int quantity;

    public CartItem() {
    }

    public CartItem(
            String productId,
            String nombre,
            String image,
            double precio,
            int quantity
    ) {
        this.productId = productId;
        this.nombre = nombre;
        this.image = image;
        this.precio = precio;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
