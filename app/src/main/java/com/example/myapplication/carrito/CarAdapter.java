package com.example.myapplication.carrito;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.home.ProductsList;

import java.text.DecimalFormat;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private List<ProductsList> productList;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartUpdated();
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public CarAdapter(List<ProductsList> productList) {
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
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder holder, int position) {

        ProductsList product = productList.get(position);

        holder.txtNombreProducto.setText(product.getNombre());
        holder.txtCantidad.setText(String.valueOf(product.getCantidad()));
        DecimalFormat df = new DecimalFormat("0.00");
        double totalPrecio = product.getPrecio() * product.getCantidad();
        holder.txtPrecio.setText("$" + df.format(totalPrecio));
        // SUMAR
        holder.btnMas.setOnClickListener(v -> {

            int q = product.getCantidad() + 1;
            product.setCantidad(q);

            holder.txtCantidad.setText(String.valueOf(q));
            holder.txtPrecio.setText("$" + (product.getPrecio() * q));

            notifyItemChanged(holder.getAdapterPosition());

            if (listener != null) listener.onCartUpdated();
        });

        // RESTAR
        holder.btnMenos.setOnClickListener(v -> {

            int q = product.getCantidad();

            if (q > 1) {
                q--;
                product.setCantidad(q);

                holder.txtCantidad.setText(String.valueOf(q));
                holder.txtPrecio.setText("$" + (product.getPrecio() * q));

                notifyItemChanged(holder.getAdapterPosition());
            } else {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    productList.remove(pos);
                    notifyItemRemoved(pos);
                }
            }
            if (listener != null) listener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateList(List<ProductsList> nuevaLista) {
        productList = nuevaLista;
        notifyDataSetChanged();
    }
}