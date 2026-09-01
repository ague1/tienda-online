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

import com.example.myapplication.R;
import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.cart.OnCartClickListener;
import com.example.myapplication.features.product.domain.model.Product;
import com.squareup.picasso.Picasso;

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
                            && Double.compare(
                            oldProduct.getPrecio(),
                            newProduct.getPrecio()
                    ) == 0
                            && Double.compare(
                            oldItem.getSpecialPrice(),
                            newItem.getSpecialPrice()
                    ) == 0
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

        Product product =
                promotionProduct.getProduct();

        double specialPrice =
                promotionProduct.getSpecialPrice();

        holder.nameProduct.setText(
                product.getNombre()
        );

        holder.textCountProduct.setText(
                product.getDescripcion()
        );

        // Precio normal
        holder.texNormalPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "$%.2f",
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
                String.format(
                        Locale.getDefault(),
                        "$%.2f",
                        promotionProduct.getSpecialPrice()
                )
        );

        holder.textSpecialPrice.setVisibility(View.VISIBLE);

        Picasso.get()
                .load(product.getImage())
                .placeholder(
                        R.drawable.ic_launcher_foreground
                )
                .error(
                        R.drawable.ic_launcher_foreground
                )
                .resizeDimen(
                        R.dimen.product_image_width,
                        R.dimen.product_image_height
                )
                .centerCrop()
                .into(holder.imageProduct);

        int cartQuantity =
                getCartQuantity(
                        product.getId()
                );

        updateQuantityView(
                holder,
                cartQuantity,
                product.getStock()
        );

        holder.addCart.setOnClickListener(v -> {

            if (product.getId() == null) {
                return;
            }

            cartClickListener.onAdd(
                    product,
                    specialPrice
            );
        });

        holder.buttonMore.setOnClickListener(v -> {

            if (product.getId() == null) {
                return;
            }

            cartClickListener.onIncrease(
                    product
            );
        });

        holder.buttonLess.setOnClickListener(v -> {

            if (product.getId() == null) {
                return;
            }

            cartClickListener.onDecrease(
                    product
            );
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

}

