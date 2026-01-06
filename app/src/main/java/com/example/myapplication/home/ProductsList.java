package com.example.myapplication.home;

public class ProductsList {

        private String image;

        public String getImage(){return image;}

        public void setImage(String image) {
                this.image = image;
        }

        public String getNombre() {
                return nombre;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public String getDescripción() {
                return Descripción;
        }

        public void setDescripción(String descripción) {
                Descripción = descripción;
        }

        public double getPrecio() {
                return precio;
        }

        public void setPrecio(double precio) {
                this.precio = precio;
        }

        public int getCantidad() {
                return cantidad;
        }

        public void setCantidad(int cantidad) {
                this.cantidad = cantidad;
        }

        private String nombre;
        private String Descripción;
        private double precio;
        private int cantidad;

        private String categoria;

        public String getCategoria(){
                return categoria;
        }

        public void setCategoria(String categoria){
                this.categoria = categoria;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        private String id;

        public boolean isFavorite() {
                return isFavorite;
        }

        public void setFavorite(boolean favorite) {
                isFavorite = favorite;
        }

        private boolean isFavorite = false;

        public int getTotalSold() {
                return totalSold;
        }

        public void setTotalSold(int totalSold) {
                this.totalSold = totalSold;
        }

        private int totalSold;

        public ProductsList() {} // Necesario para Firestore


}