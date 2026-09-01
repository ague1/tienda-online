package com.example.myapplication.features.order.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.order.CheckoutViewModel;
import com.example.myapplication.features.order.model.Order;
import com.example.myapplication.features.order.model.OrderItem;
import com.example.myapplication.features.order.adapter.CheckoutAdapter;
import com.example.myapplication.features.profiles.model.Profile;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;
    private EditText inputName, inputEmail, inputPhone, inputAddress;
    private RadioGroup paymentGroup;
    private Button btnPlaceOrder;
    private double subtotal = 0, delivery = 2.99, total = 0;
    private final DecimalFormat df = new DecimalFormat("0.00");


    TextView Subtotal, deliveryfree, Total;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initView(view);

        btnPlaceOrder.setOnClickListener(v -> {
            if (validateForm()) {
                createOrder();
            }
        });

        RecyclerView recyclerCheckout = view.findViewById(R.id.recyclerCheckout);
        recyclerCheckout.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCheckout.setAdapter(new CheckoutAdapter(viewModel.getItems()));

    }

    private boolean validateForm() {

        if (inputName.getText().toString().trim().isEmpty()) {
            inputName.setError("Required");
            return false;
        }
        if (inputEmail.getText().toString().trim().isEmpty()) {
            inputEmail.setError("Required");
            return false;
        }
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputPhone.setError("Required");
            return false;
        }
        if (inputAddress.getText().toString().trim().isEmpty()) {
            inputAddress.setError("Required");
            return false;
        }

        int selectedPayment = paymentGroup.getCheckedRadioButtonId();
        if (selectedPayment == -1) {
            Toast.makeText(getContext(), "Select payment method", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initView(View view){
        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPhone = view.findViewById(R.id.inputPhone);
        inputAddress = view.findViewById(R.id.inputAddress);
        Subtotal = view.findViewById(R.id.subtotalText);
        deliveryfree = view.findViewById(R.id.deliveryFeeText);
        Total = view.findViewById(R.id.totalText);
        paymentGroup = view.findViewById(R.id.paymentGroup);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
    }

    private void createOrder() {
        Order order = buildOrder();
// Guardar usando .document(orderId).set(order)
        viewModel.createOrder(order).addOnSuccessListener(unused -> {
            updateProfile()
                    .addOnSuccessListener(v -> {
                        if (!isAdded()) {return;}
                        Toast.makeText(requireContext(),
                                "Order placed successfully!",
                                Toast.LENGTH_LONG).show();

                        viewModel.clearCart();
                        navigateToOrders();
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) {return;}
                        Toast.makeText(requireContext(),
                                "Profile couldn't be updated",
                                Toast.LENGTH_LONG).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(),
                    "Error placing order!",
                    Toast.LENGTH_LONG).show();
        });
    }

    private void calculateTotal() {
        subtotal = viewModel.getSubtotal();
        total = subtotal + delivery;
        Subtotal.setText("Subtotal: $" + df.format(subtotal));
        deliveryfree.setText("Delivery: $" + df.format(delivery));
        Total.setText("Total: $" + df.format(total));
        // También actualizar el botón de pagar correctamente
        btnPlaceOrder.setText("Place Order - $" + df.format(total));
    }
    private Order buildOrder(){

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setName(inputName.getText().toString().trim());
        order.setEmail(inputEmail.getText().toString().trim());
        order.setPhone(inputPhone.getText().toString().trim());
        order.setAddress(inputAddress.getText().toString().trim());
        order.setPayment(getPaymentMethod());
        order.setSubtotal(subtotal);
        order.setDelivery(delivery);
        order.setTotal(total);
        order.setStatus("pending");
        order.setTimestamp(new Date());
        order.setUserId(viewModel.getCurrentUserId());
        order.setItems(buildOrderItems());

        return order;

        }

    private List<OrderItem> buildOrderItems() {
        List<OrderItem> items = new ArrayList<>();

        for(CartItem cartItem : viewModel.getItems()){
            items.add(new OrderItem(cartItem));
        }
        return items;
    }
    private String getPaymentMethod(){
        RadioButton radio = paymentGroup.findViewById(
                paymentGroup.getCheckedRadioButtonId());
        return radio.getText().toString();

    }

    private Profile buildProfile() {
        return new Profile(
                viewModel.getCurrentUserId(),
                inputName.getText().toString().trim(),
                inputEmail.getText().toString().trim(),
                inputPhone.getText().toString().trim(),
                inputAddress.getText().toString().trim()
        );
    }

    private Task<Void> updateProfile(){
        return viewModel.updateProfile(buildProfile());

    }

    private void navigateToOrders(){

        if (isAdded()) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new ClientOrderStatusFragment())
                    .addToBackStack(null)
                    .commit();
        }

    }

}

