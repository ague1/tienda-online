package com.example.myapplication.features.cart.domain.model;

import com.example.myapplication.features.product.domain.model.PricedProduct;
import com.example.myapplication.features.product.domain.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


public class Cart {

    private static final int MAX_QUANTITY = 99;

    private final Map<String, CartItem> items;

    @Inject
    public Cart() {
        items = new LinkedHashMap<>();
    }

    /**
     * Devuelve una copia de los items.
     *
     * El código externo no puede modificar directamente
     * el contenido interno del carrito.
     */
    public List<CartItem> getItems() {

        List<CartItem> copy =
                new ArrayList<>(items.size());

        for (CartItem item : items.values()) {

            copy.add(
                    item.copy()
            );
        }

        return copy;
    }
    public void addProduct(
            PricedProduct pricedProduct
    ) {

        if (pricedProduct == null) {
            return;
        }

        Product product =
                pricedProduct.getProduct();

        if (product == null) {
            return;
        }

        String productId =
                product.getId();

        if (productId == null ||
                productId.trim().isEmpty()) {
            return;
        }

        long price =
                pricedProduct.getPrice();

        CartItem existing =
                items.get(productId);

        if (existing != null) {

            existing.increase(
                    MAX_QUANTITY
            );

            return;
        }

        CartItem item =
                new CartItem(
                        productId,
                        product.getNombre(),
                        product.getImage(),
                        price,
                        1
                );

        items.put(
                productId,
                item
        );
    }


    /**
     * Actualiza únicamente el precio vigente
     * de un producto que ya está en el carrito.
     *
     * La cantidad NO cambia.
     */
    public void increase(
            String productId
    ) {

        if (productId == null) {
            return;
        }

        CartItem item =
                items.get(productId);

        if (item == null) {
            return;
        }

        item.increase(
                MAX_QUANTITY
        );
    }

    /**
     * Disminuye la cantidad.
     *
     * Si llega a cero, elimina el producto.
     */
    public void decrease(
            String productId
    ) {

        if (productId == null) {
            return;
        }

        CartItem item =
                items.get(productId);

        if (item == null) {
            return;
        }

        item.decrease();

        if (item.getQuantity() <= 0) {

            items.remove(
                    productId
            );
        }
    }

    /**
     * Elimina completamente un producto.
     */
    public void removeProduct(
            String productId
    ) {

        if (productId == null) {
            return;
        }

        items.remove(
                productId
        );
    }

    /**
     * Vacía completamente el carrito.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calcula el total usando el precio vigente
     * de cada CartItem.
     */
    public long getTotal() {

        long total = 0L;

        for (CartItem item : items.values()) {

            total +=
                    item.getPrecio()
                            * item.getQuantity();
        }

        return total;
    }

    /**
     * Reemplaza completamente el contenido
     * del carrito.
     *
     * Se utiliza principalmente al cargar/sincronizar
     * información persistida.
     */
    public void replaceItems(
            List<CartItem> newItems
    ) {

        items.clear();

        if (newItems == null ||
                newItems.isEmpty()) {
            return;
        }

        for (CartItem item : newItems) {

            if (item == null) {
                continue;
            }

            String productId =
                    item.getProductId();

            if (productId == null ||
                    productId.trim().isEmpty()) {
                continue;
            }

            long precio =
                    item.getPrecio();

            if (precio < 0) {
                continue;
            }

            int quantity =
                    item.getQuantity();

            if (quantity <= 0) {
                continue;
            }

            CartItem copy =
                    item.copy();

            copy.setQuantity(
                    quantity,
                    MAX_QUANTITY
            );

            items.put(
                    productId,
                    copy
            );
        }
    }

    /**
     * Establece una cantidad concreta.
     *
     * quantity <= 0 elimina el producto.
     */
    public void setQuantity(
            String productId,
            int quantity
    ) {

        if (productId == null) {
            return;
        }

        CartItem item =
                items.get(productId);

        if (item == null) {
            return;
        }

        if (quantity <= 0) {

            items.remove(
                    productId
            );

            return;
        }

        item.setQuantity(
                quantity,
                MAX_QUANTITY
        );
    }

    public boolean updateProductPrice(
            String productId,
            long newPrice
    ) {

        if (productId == null ||
                productId.trim().isEmpty()) {
            return false;
        }

        if (newPrice < 0) {
            return false;
        }

        CartItem item =
                items.get(productId);

        if (item == null) {
            return false;
        }

        if (item.getPrecio() == newPrice) {
            return false;
        }

        item.updatePrice(
                newPrice
        );

        return true;
    }


}
