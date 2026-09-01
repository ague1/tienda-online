package com.example.myapplication.features.cart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.cart.model.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class ClientCartAdapter extends RecyclerView.Adapter<ClientCartAdapter.ViewHolder> {
    private List<CartItem> itemList;
    private OnCartActionListener actionListener;

    public interface OnCartActionListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
    }


    public void setOnCartActionListener(OnCartActionListener listener) {
        this.actionListener = listener;
    }

    public ClientCartAdapter(List<CartItem> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombreProducto;
        TextView txtPrecio;
        TextView txtCantidad;
        Button btnMas, btnMenos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProducto);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            btnMas = itemView.findViewById(R.id.btnMas);
            btnMenos = itemView.findViewById(R.id.btnMenos);
        }
    }

    @NonNull
    @Override
    public ClientCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientCartAdapter.ViewHolder holder, int position) {

        CartItem item = itemList.get(position);

        holder.txtNombreProducto.setText(item.getNombre());
        holder.txtCantidad.setText(String.valueOf(item.getQuantity()));
        DecimalFormat df = new DecimalFormat("0.00");
        double totalPrecio = item.getPrecio() * item.getQuantity();
        holder.txtPrecio.setText("$" + df.format(totalPrecio));
        // SUMAR
        holder.btnMas.setOnClickListener(v -> {

            if (actionListener != null) {
                actionListener.onIncrease(item);
            }

        });

        // RESTAR
        holder.btnMenos.setOnClickListener(v -> {

            if (actionListener != null) {
                actionListener.onDecrease(item);
            }

        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateList( List<CartItem> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }
}