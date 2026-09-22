package com.example.myapplication.features.cart.domain.model;

public final class CartItem {

    private final String productId;
    private final String nombre;
    private final String image;
    private long precio;

    private int quantity;

    public CartItem(
            String productId,
            String nombre,
            String image,
            long precio,
            int quantity
    ) {

        if (productId == null ||
                productId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "productId no puede ser null o vacío"
            );
        }

        if (precio < 0) {

            throw new IllegalArgumentException(
                    "El precio no puede ser negativo"
            );
        }

        this.productId = productId;
        this.nombre = nombre;
        this.image = image;
        this.precio = precio;
        this.quantity = Math.max(0, quantity);
    }

    public String getProductId() {
        return productId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImage() {
        return image;
    }

    /**
     * Precio vigente del producto en el carrito.
     */
    public long getPrecio() {
        return precio;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Actualiza el precio vigente.
     *
     * Se utiliza cuando cambia el precio normal
     * o promocional del producto.
     */
    void updatePrice(long newPrice) {

        if (newPrice < 0) {
            return;
        }

        this.precio = newPrice;
    }

    /**
     * Aumenta una unidad respetando el máximo permitido.
     */
    void increase(int maxQuantity) {

        if (maxQuantity <= 0) {
            return;
        }

        if (quantity < maxQuantity) {
            quantity++;
        }
    }

    /**
     * Disminuye una unidad.
     */
    void decrease() {

        if (quantity > 0) {
            quantity--;
        }
    }

    /**
     * Establece la cantidad respetando el máximo permitido.
     */
    void setQuantity(
            int quantity,
            int maxQuantity
    ) {

        if (maxQuantity <= 0) {
            this.quantity = 0;
            return;
        }

        if (quantity <= 0) {
            this.quantity = 0;
            return;
        }

        this.quantity =
                Math.min(
                        quantity,
                        maxQuantity
                );
    }

    public CartItem copy() {

        return new CartItem(
                productId,
                nombre,
                image,
                precio,
                quantity
        );
    }
}

