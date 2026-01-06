package com.example.myapplication;

import android.content.Context;
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

import com.example.myapplication.carrito.Cart;
import com.example.myapplication.home.ProductsList;
import com.example.myapplication.pedidos.Pedido_Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutFragment extends Fragment {
    private EditText inputName, inputEmail, inputPhone, inputAddress;
    private RadioGroup paymentGroup;
    private Button btnPlaceOrder;
    private List<ProductsList> itemsCar;
    private double subtotal = 0, delivery = 2.99, total = 0;

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

        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPhone = view.findViewById(R.id.inputPhone);
        inputAddress = view.findViewById(R.id.inputAddress);

        Subtotal = view.findViewById(R.id.subtotalText);
        deliveryfree = view.findViewById(R.id.deliveryFeeText);
        Total = view.findViewById(R.id.totalText);
        paymentGroup = view.findViewById(R.id.paymentGroup);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(v -> {
            if (validateForm()) {
                saveOrderToFirebase();
            }
        });

        if (getArguments() != null) {
            itemsCar =  Cart.getInstance().getProducts();

            if (itemsCar != null && !itemsCar.isEmpty()) {
                calcularTotales();
            }

        }

        RecyclerView recyclerCheckout = view.findViewById(R.id.recyclerCheckout);
        recyclerCheckout.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCheckout.setAdapter(new CheckoutAdapter(itemsCar));


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

        Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_LONG).show();

        return true;
    }

    private void saveOrderToFirebase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Extraer método de pago
        int selectedPayment = paymentGroup.getCheckedRadioButtonId();
        RadioButton selectedBtn = getView().findViewById(selectedPayment);
        String paymentMethod = selectedBtn.getText().toString();

        // Crear mapa con datos
        Map<String, Object> order = new HashMap<>();
        order.put("name", inputName.getText().toString());
        order.put("email", inputEmail.getText().toString());
        order.put("phone", inputPhone.getText().toString());
        order.put("address", inputAddress.getText().toString());
        order.put("payment", paymentMethod);
        order.put("subtotal", subtotal);
        order.put("delivery", delivery);
        order.put("status", "pending");
        order.put("total", total);
        order.put("timestamp", new Date());
        order.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        Map<String, Object> timeline = new HashMap<>();
        timeline.put("placed", new Date());
        timeline.put("pending", new Date());
        timeline.put("confirmed", null);
        timeline.put("processing", null);
        timeline.put("delivered", null);
        order.put("timeline", timeline);

        // Convertir carrito a lista para Firestore
        List<Map<String, Object>> itemsList = new ArrayList<>();

        for (ProductsList p : itemsCar) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("nombre", p.getNombre());
            item.put("cantidad", p.getCantidad());
            item.put("precio", p.getPrecio());
            itemsList.add(item);
        }

        order.put("items", itemsList);

        // Guardar en colección "orders"
        String orderId = db.collection("orders").document().getId();

// 2️⃣ Guardar usando .document(orderId).set(order)
        db.collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(unused -> {

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Map<String, Object> userUpdate = new HashMap<>();
                    userUpdate.put("name", inputName.getText().toString());
                    userUpdate.put("email", inputEmail.getText().toString());
                    userUpdate.put("phone", inputPhone.getText().toString());
                    userUpdate.put("address", inputAddress.getText().toString());

                    db.collection("users")
                            .document(uid)
                            .set(userUpdate, SetOptions.merge());

                    // 1️⃣ Contexto seguro
                    Context appContext = getContext() != null
                            ? getContext().getApplicationContext()
                            : null;

                    if (appContext != null) {
                        Toast.makeText(appContext,
                                "Order placed successfully!", Toast.LENGTH_LONG).show();
                    }

                    // 2️⃣ Limpiar carrito
                    Cart.getInstance().getProducts().clear();

                    // 3️⃣ Navegar a fragment Pedidos SI el fragment sigue vivo
                    if (isAdded()) {
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_layout, new Pedido_Fragment())
                                .commit();
                    }

                })
                .addOnFailureListener(e -> {

                    Context appContext = getContext() != null
                            ? getContext().getApplicationContext()
                            : null;

                    if (appContext != null) {
                        Toast.makeText(appContext,
                                "Error placing order", Toast.LENGTH_LONG).show();
                    }
                });
    }

        private void calcularTotales() {

            DecimalFormat df = new DecimalFormat("0.00");

            subtotal = 0;

            for (ProductsList p : itemsCar) {
                subtotal += p.getPrecio() * p.getCantidad();
            }

            total = subtotal + delivery;

            Subtotal.setText("Subtotal: $" + df.format(subtotal));
            deliveryfree.setText("Delivery: $" + df.format(delivery));
            Total.setText("Total: $" + df.format(total));

            // También actualizar el botón de pagar correctamente
            btnPlaceOrder.setText("Place Order - $" + df.format(total));
        }
    }
