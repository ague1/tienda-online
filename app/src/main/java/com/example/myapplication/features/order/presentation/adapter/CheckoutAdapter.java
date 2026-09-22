package com.example.myapplication.features.order.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.features.cart.domain.model.CartItem;

import java.util.Locale;
import java.util.Objects;

public class CheckoutAdapter
        extends ListAdapter<CartItem, CheckoutAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CartItem>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull CartItem oldItem,
                        @NonNull CartItem newItem
                ) {
                    return Objects.equals(
                            oldItem.getProductId(),
                            newItem.getProductId()
                    );
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull CartItem oldItem,
                        @NonNull CartItem newItem
                ) {
                    return Objects.equals(
                            oldItem.getProductId(),
                            newItem.getProductId()
                    )
                            && Objects.equals(
                            oldItem.getNombre(),
                            newItem.getNombre()
                    )
                            && Objects.equals(
                            oldItem.getImage(),
                            newItem.getImage()
                    )
                            && oldItem.getPrecio()
                            == newItem.getPrecio()
                            && oldItem.getQuantity()
                            == newItem.getQuantity();
                }
            };

    public CheckoutAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_checkout,
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

        CartItem cartItem = getItem(position);

        long total =
                cartItem.getPrecio()
                        * cartItem.getQuantity();

        holder.name.setText(
                cartItem.getNombre()
        );

        holder.qty.setText(
                "Qty: " + cartItem.getQuantity()
        );

        holder.price.setText(
                "$" + formatMoney(total)
        );

        Glide.with(holder.image.getContext())
                .clear(holder.image);

        Glide.with(holder.image.getContext())
                .load(cartItem.getImage())
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_product_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .into(holder.image);

    }

    private String formatMoney(long cents) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                cents / 100.0
        );
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView name;
        TextView qty;
        TextView price;
        ImageView image;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            name = itemView.findViewById(
                    R.id.txtProductName
            );

            qty = itemView.findViewById(
                    R.id.txtProductQty
            );

            price = itemView.findViewById(
                    R.id.txtProductPrice
            );

            image = itemView.findViewById(
                    R.id.imgProduct
            );
        }
    }
}
