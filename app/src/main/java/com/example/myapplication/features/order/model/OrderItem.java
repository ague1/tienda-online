package com.example.myapplication.features.order.model;

import com.example.myapplication.features.product.model.Product;

public class OrderItem {

    private String id;
    private String nombre;
    private int cantidad;
    private double precio;

    public OrderItem() {}

    public OrderItem(Product product) {
        this.id = product.getId();
        this.nombre = product.getNombre();
        this.cantidad = product.getCantidad();
        this.precio = product.getPrecio();
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
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
