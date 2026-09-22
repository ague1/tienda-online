package com.example.myapplication.features.order.domain.model;
public class OrderItem {

    private String id;
    private String nombre;
    private int quantity;
    private long precio;

    public OrderItem() {}

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
        this.quantity = cantidad;
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }
}
