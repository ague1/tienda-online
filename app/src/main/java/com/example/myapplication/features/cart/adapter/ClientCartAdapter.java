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
import com.example.myapplication.features.product.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ClientCartAdapter extends RecyclerView.Adapter<ClientCartAdapter.ViewHolder> {
    private List<Product> productList;
    private OnCartActionListener actionListener;

    public interface OnCartActionListener {
        void onIncrease(Product product);
        void onDecrease(Product product);
    }


    public void setOnCartActionListener(OnCartActionListener listener) {
        this.actionListener = listener;
    }

    public ClientCartAdapter(List<Product> productList) {
        this.productList = productList;
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

        Product product = productList.get(position);

        holder.txtNombreProducto.setText(product.getNombre());
        holder.txtCantidad.setText(String.valueOf(product.getCantidad()));
        DecimalFormat df = new DecimalFormat("0.00");
        double totalPrecio = product.getPrecio() * product.getCantidad();
        holder.txtPrecio.setText("$" + df.format(totalPrecio));
        // SUMAR
        holder.btnMas.setOnClickListener(v -> {

            if (actionListener != null) {
                actionListener.onIncrease(product);
            }

        });

        // RESTAR
        holder.btnMenos.setOnClickListener(v -> {

            if (actionListener != null) {
                actionListener.onDecrease(product);
            }

        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateList(List<Product> nuevaLista) {
        productList = nuevaLista;
        notifyDataSetChanged();
    }
}