package com.example.myapplication.features.order.model;

import com.example.myapplication.features.cart.model.CartItem;

public class OrderItem {

    private String id;
    private String nombre;
    private int quantity;
    private double precio;

    public OrderItem() {}

    public OrderItem(CartItem cartItem) {
        this.id = cartItem.getProductId();
        this.nombre = cartItem.getNombre();
        this.quantity = cartItem.getQuantity();
        this.precio = cartItem.getPrecio();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return quantity;
    }

    public void setCantidad(int cantidad) {
        this.quantity= cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
