package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.home.ProductsList;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {
    private List<ProductsList> list;

    public CheckoutAdapter(List<ProductsList> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductsList p = list.get(position);

        holder.name.setText(p.getNombre());
        holder.qty.setText("Qty: " + p.getCantidad());
        holder.price.setText("$" + p.getPrecio() * p.getCantidad());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtProductName);
            qty = itemView.findViewById(R.id.txtProductQty);
            price = itemView.findViewById(R.id.txtProductPrice);
            image = itemView.findViewById(R.id.imgProduct);
        }
    }
}
