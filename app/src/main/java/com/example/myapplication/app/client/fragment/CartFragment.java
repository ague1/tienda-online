package com.example.myapplication.app.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.domain.Cart;
import com.example.myapplication.R;
import com.example.myapplication.domain.model.Product;
import com.example.myapplication.app.client.adapter.ClientCartAdapter;

import java.io.Serializable;

public class CartFragment extends Fragment {
    private RecyclerView recyclerCarrito;
    private ClientCartAdapter adapter;
    private TextView txtTotal;
    private Button buttonCheckout;

    public CartFragment() {
        // Required empty constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_car, container, false);

        recyclerCarrito = view.findViewById(R.id.recyclerCarrito);
        txtTotal = view.findViewById(R.id.txtTotal);
        buttonCheckout = view.findViewById(R.id.btnPagar);

        recyclerCarrito.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ClientCartAdapter(Cart.getInstance().getProducts());
        recyclerCarrito.setAdapter(adapter);

        adapter.setOnCartChangeListener(() -> actualizarTotal());
        actualizarTotal();

        buttonCheckout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("items_car", (Serializable) Cart.getInstance().getProducts());

            CheckoutFragment checkoutFragment = new CheckoutFragment();
            checkoutFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, checkoutFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateList(Cart.getInstance().getProducts());
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        for (Product p : Cart.getInstance().getProducts()) {
            total += p.getPrecio() * p.getCantidad();
        }
        txtTotal.setText(String.format("Total: $%.2f", total));
    }
}
