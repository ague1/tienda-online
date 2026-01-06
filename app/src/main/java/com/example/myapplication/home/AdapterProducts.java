package com.example.myapplication.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.carrito.Cart;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolder> implements Filterable {
    private List<ProductsList> productList;//
    private List<ProductsList> productListFull;
    private Context context;

    //Constructor
    public AdapterProducts(Context context, List<ProductsList> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
    }

    // 🔹 1. Definir el ViewHolder o vista de XML
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView nameProduct;
        TextView textCountProduct;
        TextView textPrice;
        Button addCart;
        LinearLayout linearLayout;
        Button buttonLess;
        TextView textAmount;
        Button buttonMore;


        public ViewHolder(View itemView) {//esto es asi siempre, si o si
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imgProducto);
            nameProduct = itemView.findViewById(R.id.txtNombreProducto);
            textCountProduct = itemView.findViewById(R.id.txtCount);
            textPrice = itemView.findViewById(R.id.txtPrecio);
            addCart = itemView.findViewById(R.id.btnAñadirCarrito);
            linearLayout = itemView.findViewById(R.id.layoutContador);
            buttonLess = itemView.findViewById(R.id.btnMenos);
            textAmount = itemView.findViewById(R.id.txtCantidad);
            buttonMore = itemView.findViewById(R.id.btnMas);// debe coincidir con tu layout XML
        }
    }

    // 🔹 2. Inflar el layout del ítem
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items, parent, false); // tu layout aquí
        return new ViewHolder(view);
    }

    // 🔹 3. Vincular los datos
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductsList product = productList.get(position);
        holder.nameProduct.setText(product.getNombre());
        holder.textCountProduct.setText(product.getDescripción());
        holder.textPrice.setText("$" + product.getPrecio());

        Picasso.get()
                .load(product.getImage()) // 🔥 URL Firestore
                .into(holder.imageProduct);

        int quantity = product.getCantidad();

        holder.addCart.setOnClickListener(v -> {
            Cart.getInstance().addProduct(product);
            holder.addCart.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        });

        holder.textAmount.setText(String.valueOf(quantity));
        holder.buttonMore.setOnClickListener(v -> {
            int q = product.getCantidad() + 1;
            product.setCantidad(q);
            holder.textAmount.setText(String.valueOf(q));

            // Actualizar en el carrito
            Cart.getInstance().addProduct(product);
        });
        holder.buttonLess.setOnClickListener(v -> {
            int q = product.getCantidad();
            if (q > 1) {
                q--;
                product.setCantidad(q);
                holder.textAmount.setText(String.valueOf(q));
            } else {
                q = 0;
                product.setCantidad(q);
                holder.addCart.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Seleccionaste: " + product.getNombre(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        return filtro;
    }

    private Filter filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductsList> filtrados = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filtrados.addAll(productListFull);
            } else {
                String texto = constraint.toString().toLowerCase().trim();

                for (ProductsList item : productListFull) {
                    if (item.getNombre().toLowerCase().contains(texto) ||
                            (item.getCategoria() != null && item.getCategoria().toLowerCase().contains(texto))) {

                        filtrados.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtrados;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            if (results.values != null) {
                productList.addAll((List<ProductsList>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    public void updateFullList(List<ProductsList> newList) {
        productListFull.clear();
        productListFull.addAll(newList);
    }

    public void updateList(List<ProductsList> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    /*holder.itemView.setOnClickListener(v -> {
    Intent i = new Intent(context, OrderProcessActivity.class);
    i.putExtra("orderId", doc.getId());
    context.startActivity(i);
});*/
}
