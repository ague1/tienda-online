package com.example.myapplication.features.cart.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class ClientCartAdapter
        extends ListAdapter<CartItem, ClientCartAdapter.ViewHolder> {

    private OnCartActionListener actionListener;

    public interface OnCartActionListener {

        void onIncrease(CartItem item);

        void onDecrease(CartItem item);
    }

    private static final DiffUtil.ItemCallback<CartItem>
            DIFF_CALLBACK =
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

    public ClientCartAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnCartActionListener(
            OnCartActionListener listener
    ) {
        this.actionListener = listener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombreProducto;
        TextView txtPrecio;
        TextView txtCantidad;
        Button btnMas;
        Button btnMenos;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            imgProducto =
                    itemView.findViewById(
                            R.id.imgProducto
                    );

            txtNombreProducto =
                    itemView.findViewById(
                            R.id.txtNombreProducto
                    );

            txtPrecio =
                    itemView.findViewById(
                            R.id.txtPrecio
                    );

            txtCantidad =
                    itemView.findViewById(
                            R.id.txtCantidad
                    );

            btnMas =
                    itemView.findViewById(
                            R.id.btnMas
                    );

            btnMenos =
                    itemView.findViewById(
                            R.id.btnMenos
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
                        R.layout.item_car,
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

        CartItem item = getItem(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(352, 240)
                .centerCrop()
                .into(holder.imgProducto);


        holder.txtNombreProducto.setText(
                item.getNombre()
        );

        holder.txtCantidad.setText(
                String.valueOf(
                        item.getQuantity()
                )
        );

        holder.txtPrecio.setText(
                "$" + formatMoney(item.getPrecio())
        );


        holder.btnMas.setOnClickListener(v -> {

            if (actionListener != null) {

                actionListener.onIncrease(item);
            }
        });

        holder.btnMenos.setOnClickListener(v -> {

            if (actionListener != null) {

                actionListener.onDecrease(item);
            }
        });
    }

    private String formatMoney(long cents) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                cents / 100.0
        );
    }

}