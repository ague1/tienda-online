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
import com.example.myapplication.features.auth.repository.AuthRepository;
import com.example.myapplication.features.cart.repository.CartRepository;
import com.example.myapplication.features.order.model.Order;
import com.example.myapplication.features.order.model.OrderItem;
import com.example.myapplication.features.order.usecase.CreateOrderUseCase;
import com.example.myapplication.features.product.model.Product;
import com.example.myapplication.features.order.adapter.CheckoutAdapter;
import com.example.myapplication.features.profiles.model.Profile;
import com.example.myapplication.features.profiles.usecase.UpdateProfileUseCase;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CheckoutFragment extends Fragment {
    @Inject
    CartRepository cartRepository;
    @Inject
    UpdateProfileUseCase updateProfileUseCase;
    @Inject
    AuthRepository auth;
    @Inject
    CreateOrderUseCase createOrderUseCase;
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

        if (!cartRepository.getProducts().isEmpty()) {
            calculateTotal();
        }

        RecyclerView recyclerCheckout = view.findViewById(R.id.recyclerCheckout);
        recyclerCheckout.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCheckout.setAdapter(new CheckoutAdapter(cartRepository.getProducts()));

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
        createOrderUseCase.execute(order).addOnSuccessListener(unused -> {
            updateProfile()
                    .addOnSuccessListener(v -> {
                        Toast.makeText(requireContext(),
                                "Order placed successfully!",
                                Toast.LENGTH_LONG).show();

                        cartRepository.clearCart();
                        navigateToOrders();
                    })
                    .addOnFailureListener(e -> {
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
        subtotal = cartRepository.getTotal();
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
        order.setUserId(auth.getCurrentUserId());
        order.setItems(buildOrderItems());

        return order;

        }

    private List<OrderItem> buildOrderItems() {
        List<OrderItem> items = new ArrayList<>();

        for(Product product : cartRepository.getProducts()){
            items.add(new OrderItem(product));
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
                auth.getCurrentUserId(),
                inputName.getText().toString().trim(),
                inputEmail.getText().toString().trim(),
                inputPhone.getText().toString().trim(),
                inputAddress.getText().toString().trim()
        );
    }

    private Task<Void> updateProfile(){
        return updateProfileUseCase.execute(buildProfile());

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

