package com.example.myapplication.features.product.domain.model;

public class Product {

        private final String id;

        private final String nombre;
        private final String descripcion;
        private final String categoria;

        private final double precio;
        private final int stock;
        private final int totalSold;

        private final String image;

        public Product(
                String id,
                String nombre,
                String descripcion,
                String categoria,
                double precio,
                int stock,
                int totalSold,
                String image
        ) {
                this.id = id;
                this.nombre = nombre;
                this.descripcion = descripcion;
                this.categoria = categoria;
                this.precio = precio;
                this.stock = stock;
                this.totalSold = totalSold;
                this.image = image;
        }

        public String getId() {
                return id;
        }



        public String getNombre() {
                return nombre;
        }

        public String getDescripcion() {
                return descripcion;
        }

        public String getCategoria() {
                return categoria;
        }

        public double getPrecio() {
                return precio;
        }
        public int getStock() {
                return stock;
        }
        public int getTotalSold() {
                return totalSold;
        }
        public String getImage() {
                return image;
        }

}
