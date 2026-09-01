package com.example.myapplication.features.product.presentation.home.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.product.domain.model.Product;
import com.example.myapplication.features.cart.OnCartClickListener;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClientProductsAdapter
        extends ListAdapter<Product, ClientProductsAdapter.ViewHolder> {

    private final OnCartClickListener cartClickListener;

    // productId -> cantidad actual en carrito
    private final Map<String, Integer> cartQuantities =
            new HashMap<>();

    private final Map<String, Double> specialPrices =
            new HashMap<>();

    public ClientProductsAdapter(
            OnCartClickListener cartClickListener
    ) {
        super(DIFF_CALLBACK);
        this.cartClickListener = cartClickListener;
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull Product oldItem,
                        @NonNull Product newItem
                ) {
                    return Objects.equals(
                            oldItem.getId(),
                            newItem.getId()
                    );
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Product oldItem,
                        @NonNull Product newItem
                ) {
                    return Objects.equals(
                            oldItem.getNombre(),
                            newItem.getNombre()
                    )
                            && Objects.equals(
                            oldItem.getDescripcion(),
                            newItem.getDescripcion()
                    )
                            && Double.compare(
                            oldItem.getPrecio(),
                            newItem.getPrecio()
                    ) == 0
                            && Objects.equals(
                            oldItem.getImage(),
                            newItem.getImage()
                    )
                            && Objects.equals(
                            oldItem.getCategoria(),
                            newItem.getCategoria()
                    )
                            && oldItem.getStock()
                            == newItem.getStock()
                            && oldItem.getTotalSold()
                            == newItem.getTotalSold();
                }
            };


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView textSpecialPrice;
        TextView textNormalPrice;

        ImageView imageProduct;
        TextView nameProduct;
        TextView textCountProduct;

        Button addCart;
        LinearLayout linearLayout;
        Button buttonLess;
        TextView textAmount;
        Button buttonMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.imgProducto);
            nameProduct = itemView.findViewById(R.id.txtNombreProducto);
            textCountProduct = itemView.findViewById(R.id.txtCount);
            textNormalPrice = itemView.findViewById(R.id.txtPrecioNormal);
            textSpecialPrice = itemView.findViewById(R.id.txtPrecioEspecial);
            addCart = itemView.findViewById(R.id.btnAnadirCarrito);
            linearLayout = itemView.findViewById(R.id.layoutContador);
            buttonLess = itemView.findViewById(R.id.btnMenos);
            textAmount = itemView.findViewById(R.id.txtCantidad);
            buttonMore = itemView.findViewById(R.id.btnMas);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items, parent, false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = getItem(position);

        holder.nameProduct.setText(
                product.getNombre()
        );

        holder.textCountProduct.setText(
                product.getDescripcion()
        );

        Double specialPrice =
                specialPrices.get(product.getId());

        if (specialPrice != null) {

            holder.textNormalPrice.setVisibility(View.GONE);

            holder.textSpecialPrice.setText(
                    String.format(
                            Locale.getDefault(),
                            "$%.2f",
                            specialPrice
                    )
            );

            holder.textSpecialPrice.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.textNormalPrice.setText(
                    String.format(
                            Locale.getDefault(),
                            "$%.2f",
                            product.getPrecio()
                    )
            );

            holder.textNormalPrice.setVisibility(
                    View.VISIBLE
            );

            holder.textSpecialPrice.setVisibility(
                    View.GONE
            );
        }


        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(352, 240)
                .centerCrop()
                .into(holder.imageProduct);

        int cartQuantity =
                getCartQuantity(product.getId());

        updateQuantityView(holder, cartQuantity, product.getStock()
        );

        // AGREGAR
        holder.addCart.setOnClickListener(v -> {

            if (product.getId() == null) {
                return;
            }

            double effectivePrice =
                    specialPrice != null
                            ? specialPrice
                            : product.getPrecio();

            cartClickListener.onAdd(
                    product,
                    effectivePrice
            );
        });


        holder.buttonMore.setOnClickListener(v -> {

            if (product.getId() == null) {
                return;
            }

            cartClickListener.onIncrease(product);
        });

        holder.buttonLess.setOnClickListener(v -> {

            if (product.getId() == null) {
                return;
            }

            cartClickListener.onDecrease(product);
        });

        holder.itemView.setOnClickListener(v ->

            Toast.makeText(
                    v.getContext(),
                    "Seleccionaste: "
                            + product.getNombre(),
                    Toast.LENGTH_SHORT
            ).show()
        );
    }

    private int getCartQuantity(String productId) {

        if (productId == null) {
            return 0;
        }

        Integer quantity = cartQuantities.get(productId);

        return quantity != null
                ? quantity
                : 0;

    }


    public void updatePromotions(
            List<PromotionProduct> promotions
    ) {

        Map<String, Double> newPrices =
                buildSpecialPrices(promotions);

        Set<String> affectedIds =
                new HashSet<>(specialPrices.keySet());

        affectedIds.addAll(newPrices.keySet());

        specialPrices.clear();
        specialPrices.putAll(newPrices);

        for (int i = 0; i < getItemCount(); i++) {

            Product product = getItem(i);

            String productId = product.getId();

            if (productId != null
                    && affectedIds.contains(productId)) {

                notifyItemChanged(i);
            }
        }
    }

    private Map<String, Double> buildSpecialPrices(
            List<PromotionProduct> promotions
    ) {

        Map<String, Double> newPrices =
                new HashMap<>();

        if (promotions == null) {
            return newPrices;
        }

        for (PromotionProduct promotion : promotions) {

            if (promotion == null
                    || promotion.getProduct() == null
                    || promotion.getProduct().getId() == null) {
                continue;
            }

            newPrices.put(
                    promotion.getProduct().getId(),
                    promotion.getSpecialPrice()
            );
        }

        return newPrices;
    }




    private void updateQuantityView(
            ViewHolder holder,
            int quantity,
            int stock
    ) {

        holder.textAmount.setText(
                String.valueOf(quantity)
        );

        if (quantity > 0) {

            holder.addCart.setVisibility(
                    View.GONE
            );

            holder.linearLayout.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.addCart.setVisibility(
                    View.VISIBLE
            );

            holder.linearLayout.setVisibility(
                    View.GONE
            );
        }

        // No permitir superar el stock
        holder.buttonMore.setEnabled(
                quantity < stock
        );

        // No permitir bajar de 0
        holder.buttonLess.setEnabled(
                quantity > 0
        );

        // Si no hay stock, no permitir agregar
        holder.addCart.setEnabled(
                stock > 0
        );
    }

    public void updateCartQuantities(
            List<CartItem> items
    ) {

        Map<String, Integer> oldQuantities =
                new HashMap<>(cartQuantities);

        cartQuantities.clear();

        if (items == null) {
            items = Collections.emptyList();
        }

        for (CartItem item : items) {

            if (item.getProductId() != null) {

                cartQuantities.put(
                        item.getProductId(),
                        item.getQuantity()
                );
            }
        }

        for (int i = 0; i < getItemCount(); i++) {

            Product product = getItem(i);

            String productId = product.getId();

            if (productId == null) {
                continue;
            }

            Integer oldQuantityValue =
                    oldQuantities.get(productId);

            int oldQuantity =
                    oldQuantityValue != null
                            ? oldQuantityValue
                            : 0;


            Integer newQuantityValue =
                    cartQuantities.get(productId);

            int newQuantity =
                    newQuantityValue != null
                            ? newQuantityValue
                            : 0;


            if (oldQuantity != newQuantity) {

                notifyItemChanged(i);
            }
        }
    }

}