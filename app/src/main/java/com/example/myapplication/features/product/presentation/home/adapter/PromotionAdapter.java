package com.example.myapplication.features.product.presentation.home.adapter;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.product.domain.model.PricedProduct;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.cart.presentation.listener.OnCartClickListener;
import com.example.myapplication.features.product.domain.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PromotionAdapter
        extends ListAdapter<PromotionProduct, PromotionAdapter.ViewHolder> {

    private final OnCartClickListener cartClickListener;

    private final Map<String, Integer> cartQuantities =
            new HashMap<>();

    public PromotionAdapter(
            OnCartClickListener cartClickListener
    ) {
        super(DIFF_CALLBACK);
        this.cartClickListener = cartClickListener;
    }

    private static final DiffUtil.ItemCallback<PromotionProduct>
            DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull PromotionProduct oldItem,
                        @NonNull PromotionProduct newItem
                ) {

                    return Objects.equals(
                            oldItem.getProduct().getId(),
                            newItem.getProduct().getId()
                    );
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull PromotionProduct oldItem,
                        @NonNull PromotionProduct newItem
                ) {

                    Product oldProduct =
                            oldItem.getProduct();

                    Product newProduct =
                            newItem.getProduct();

                    return Objects.equals(
                            oldProduct.getNombre(),
                            newProduct.getNombre()
                    )
                            && Objects.equals(
                            oldProduct.getDescripcion(),
                            newProduct.getDescripcion()
                    )
                            && oldProduct.getPrecio()
                            == newProduct.getPrecio()
                            && oldItem.getSpecialPrice()
                            == newItem.getSpecialPrice()
                            && Objects.equals(
                            oldProduct.getImage(),
                            newProduct.getImage()
                    )
                            && oldProduct.getStock()
                            == newProduct.getStock();
                }
            };

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imageProduct;
        TextView nameProduct;
        TextView textCountProduct;
        TextView textSpecialPrice;
        TextView texNormalPrice;
        Button addCart;
        LinearLayout linearLayout;
        Button buttonLess;
        TextView textAmount;
        Button buttonMore;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            imageProduct =
                    itemView.findViewById(
                            R.id.imgProducto
                    );

            nameProduct =
                    itemView.findViewById(
                            R.id.txtNombreProducto
                    );

            textCountProduct =
                    itemView.findViewById(
                            R.id.txtCount
                    );

            texNormalPrice =
                    itemView.findViewById(
                            R.id.txtPrecioNormal
                    );

            textSpecialPrice =
                    itemView.findViewById(
                            R.id.txtPrecioEspecial
                    );

            addCart =
                    itemView.findViewById(
                            R.id.btnAnadirCarrito
                    );

            linearLayout =
                    itemView.findViewById(
                            R.id.layoutContador
                    );

            buttonLess =
                    itemView.findViewById(
                            R.id.btnMenos
                    );

            textAmount =
                    itemView.findViewById(
                            R.id.txtCantidad
                    );

            buttonMore =
                    itemView.findViewById(
                            R.id.btnMas
                    );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.items,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        PromotionProduct promotionProduct =
                getItem(position);

        if (promotionProduct == null ||
                promotionProduct.getProduct() == null) {
            return;
        }

        Product product =
                promotionProduct.getProduct();

        String productId =
                product.getId();

        if (productId == null ||
                productId.trim().isEmpty()) {
            return;
        }

        long specialPrice =
                promotionProduct.getSpecialPrice();

        holder.nameProduct.setText(
                product.getNombre()
        );

        holder.textCountProduct.setText(
                product.getDescripcion()
        );

        holder.texNormalPrice.setText(
                "$" + formatMoney(
                        product.getPrecio()
                )
        );

        holder.texNormalPrice.setPaintFlags(
                holder.texNormalPrice.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG
        );

        holder.texNormalPrice.setVisibility(
                View.VISIBLE
        );

        holder.textSpecialPrice.setText(
                "$" + formatMoney(
                        specialPrice
                )
        );

        holder.textSpecialPrice.setVisibility(
                View.VISIBLE
        );


        Glide.with(
                        holder.imageProduct.getContext()
                )
                .clear(holder.imageProduct);

        Glide.with(
                        holder.imageProduct.getContext()
                )
                .load(product.getImage())
                .placeholder(
                        R.drawable.ic_product_placeholder
                )
                .error(
                        R.drawable.ic_product_placeholder
                )
                .diskCacheStrategy(
                        DiskCacheStrategy.AUTOMATIC
                )
                .centerCrop()
                .into(holder.imageProduct);


        int cartQuantity =
                getCartQuantity(productId);

        updateQuantityView(
                holder,
                cartQuantity,
                product.getStock()
        );

        holder.addCart.setOnClickListener(v -> {

            PricedProduct pricedProduct =
                    new PricedProduct(
                            product,
                            specialPrice
                    );

            cartClickListener.onAdd(
                    pricedProduct
            );
        });

        holder.buttonMore.setOnClickListener(v -> {

            PricedProduct pricedProduct =
                    new PricedProduct(
                            product,
                            specialPrice
                    );

            cartClickListener.onIncrease(
                    pricedProduct
            );
        });


        holder.buttonLess.setOnClickListener(v -> {

            PricedProduct pricedProduct =
                    new PricedProduct(
                            product,
                            specialPrice
                    );

            cartClickListener.onDecrease(
                    pricedProduct
            );
        });


        holder.itemView.setOnClickListener(v -> {

            Toast.makeText(
                    v.getContext(),
                    "Seleccionaste: "
                            + product.getNombre(),
                    Toast.LENGTH_SHORT
            ).show();
        });

    }

    private int getCartQuantity(
            String productId
    ) {

        if (productId == null) {
            return 0;
        }

        Integer quantity =
                cartQuantities.get(productId);

        return quantity != null
                ? quantity
                : 0;
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

        holder.buttonMore.setEnabled(
                quantity < stock
        );

        holder.buttonLess.setEnabled(
                quantity > 0
        );

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

        if (items != null) {

            for (CartItem item : items) {

                if (item.getProductId() != null) {

                    cartQuantities.put(
                            item.getProductId(),
                            item.getQuantity()
                    );
                }
            }
        }

        for (int i = 0; i < getItemCount(); i++) {

            PromotionProduct promotionProduct =
                    getItem(i);

            Product product =
                    promotionProduct.getProduct();

            String productId =
                    product.getId();

            Integer oldQuantity =
                    oldQuantities.get(productId);

            Integer newQuantity =
                    cartQuantities.get(productId);

            if (!Objects.equals(
                    oldQuantity,
                    newQuantity
            )) {

                notifyItemChanged(i);
            }
        }
    }
    private String formatMoney(long cents) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                cents / 100.0
        );
    }
}
