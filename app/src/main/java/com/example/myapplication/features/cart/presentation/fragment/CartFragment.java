package com.example.myapplication.features.cart.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.features.cart.presentation.viewmodel.CartViewModel;
import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.order.presentation.fragment.CheckoutFragment;
import com.example.myapplication.R;
import com.example.myapplication.features.cart.presentation.adapter.ClientCartAdapter;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends Fragment {
    private RecyclerView recyclerCarrito;
    private ClientCartAdapter adapter;
    private TextView txtTotal;
    private Button buttonCheckout;
    private CartViewModel viewModel;

    private boolean hasCartItems;

    public CartFragment() {
        // Required empty constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_car, container, false);
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        recyclerCarrito = view.findViewById(R.id.recyclerCarrito);
        txtTotal = view.findViewById(R.id.txtTotal);
        buttonCheckout = view.findViewById(R.id.btnPagar);

        recyclerCarrito.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ClientCartAdapter();
        recyclerCarrito.setAdapter(adapter);

        observeCart();
        initCartActions();
        initCheckout();

        return view;
    }

    private void observeCart() {

        viewModel.getItems().observe(
                getViewLifecycleOwner(),
                items -> {

                    adapter.submitList(items);
                    hasCartItems = items != null && !items.isEmpty();
                    buttonCheckout.setEnabled(hasCartItems);
                }
        );

        viewModel.getTotal().observe(
                getViewLifecycleOwner(),
                total -> {

                    if (total == null) {
                        total = 0L;
                    }

                    txtTotal.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "$%.2f",
                                    total / 100.0
                            )
                    );
                }
        );

    }
    private void initCheckout() {
        buttonCheckout.setOnClickListener(v -> {

            if (!hasCartItems) {
                Toast.makeText(
                        requireContext(),
                        "Your cart is empty",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.frame_layout,
                            new CheckoutFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }


    private void initCartActions() {adapter.setOnCartActionListener(
            new ClientCartAdapter.OnCartActionListener() {

                @Override
                public void onIncrease(CartItem item) {
                    viewModel.increase(item.getProductId());
                }
                @Override
                public void onDecrease(CartItem item) {
                    viewModel.decrease(item.getProductId());
                }
            });
    }
}
