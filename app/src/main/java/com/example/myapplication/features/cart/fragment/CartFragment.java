package com.example.myapplication.features.cart.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.features.order.fragment.CheckoutFragment;
import com.example.myapplication.R;
import com.example.myapplication.features.cart.repository.CartRepository;
import com.example.myapplication.features.product.model.Product;
import com.example.myapplication.features.cart.adapter.ClientCartAdapter;

import java.io.Serializable;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends Fragment {
    @Inject
    CartRepository cartRepository;
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

        adapter = new ClientCartAdapter(cartRepository.getProducts());
        recyclerCarrito.setAdapter(adapter);

        adapter.setOnCartActionListener(new ClientCartAdapter.OnCartActionListener() {
            @Override
            public void onIncrease(Product product) {
                cartRepository.increase(product);
                adapter.notifyDataSetChanged();
                updateTotal();
            }
            @Override
            public void onDecrease(Product product) {
                cartRepository.decrease(product);
                adapter.notifyDataSetChanged();
                updateTotal();
            }
        });

        buttonCheckout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("items_car", (Serializable) cartRepository.getProducts());

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
        adapter.updateList(cartRepository.getProducts());
        updateTotal();
    }

    private void updateTotal() {
        double total = cartRepository.getTotal();
        txtTotal.setText(String.format("Total: $%.2f", total));
    }
}
