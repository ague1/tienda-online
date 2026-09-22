package com.example.myapplication.features.order.presentation.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.order.presentation.viewmodel.CheckoutViewModel;
import com.example.myapplication.features.order.presentation.adapter.CheckoutAdapter;
import com.example.myapplication.features.order.domain.request.CreateOrderRequest;
import com.example.myapplication.features.order.domain.request.OrderItemRequest;
import com.example.myapplication.features.profiles.domain.model.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPhone;
    private EditText inputAddress;

    private RadioGroup paymentGroup;

    private Button btnPlaceOrder;

    private TextView Subtotal;
    private TextView deliveryfree;
    private TextView Total;

    private EditText inputDate;
    private EditText inputTime;
    private long subtotal = 0L;
    private long delivery = 299L;
    private long total = 0L;

    private List<CartItem> currentCartItems =
            new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_checkout,
                container,
                false
        );
    }


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        viewModel =
                new ViewModelProvider(this)
                        .get(CheckoutViewModel.class);

        initView(view);

        setupRecyclerView(view);

        observeSubtotal();
        observeOrderState();
        btnPlaceOrder.setOnClickListener(v -> {

            if (validateForm()) {
                createOrder();
            }
        });

        setupDatePicker();
        setupTimePicker();

    }

    private void observeOrderState() {

        viewModel.getOrderState().observe(getViewLifecycleOwner(),
                state -> {

                    if (state == null) {
                        return;
                    }

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:
                            btnPlaceOrder.setEnabled(false);
                            btnPlaceOrder.setText("Placing order...");
                            break;

                        case SUCCESS:

                            Boolean profileUpdated =
                                    state.getData();

                            if (!isAdded()) {
                                return;
                            }

                            if (Boolean.TRUE.equals(profileUpdated)) {

                                Toast.makeText(
                                        requireContext(),
                                        "Order placed successfully!",
                                        Toast.LENGTH_LONG
                                ).show();

                            } else {

                                Toast.makeText(
                                        requireContext(),
                                        "Order placed successfully, but profile couldn't be updated.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            navigateToOrders();
                            break;

                        case ERROR:

                            btnPlaceOrder.setEnabled(true);
                            btnPlaceOrder.setText(
                                    "Place Order - " +
                                            formatMoney(total)
                            );

                            if (!isAdded()) {
                                return;
                            }

                            Toast.makeText(
                                    requireContext(),
                                    "Error placing order: " +
                                            state.getError(),
                                    Toast.LENGTH_LONG
                            ).show();

                            break;
                    }
                }
        );
    }



    private void setupTimePicker() {

        inputTime.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            int hour =
                    calendar.get(Calendar.HOUR_OF_DAY);

            int minute =
                    calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog =
                    new TimePickerDialog(
                            requireContext(),
                            (view, selectedHour,
                             selectedMinute) -> {

                                String time =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d:%02d",
                                                selectedHour,
                                                selectedMinute
                                        );

                                inputTime.setText(time);
                            },
                            hour,
                            minute,
                            true
                    );

            dialog.show();
        });
    }



    private void setupDatePicker() {

        inputDate.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            int year =
                    calendar.get(Calendar.YEAR);

            int month =
                    calendar.get(Calendar.MONTH);

            int day =
                    calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            requireContext(),
                            (view, selectedYear,
                             selectedMonth,
                             selectedDay) -> {

                                String date =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                selectedDay,
                                                selectedMonth + 1,
                                                selectedYear
                                        );

                                inputDate.setText(date);
                            },
                            year,
                            month,
                            day
                    );

            // No permitir fechas anteriores a hoy.
            dialog.getDatePicker()
                    .setMinDate(
                            System.currentTimeMillis()
                    );

            dialog.show();
        });
    }



    private void setupRecyclerView(View view) {

        RecyclerView recyclerCheckout =
                view.findViewById(
                        R.id.recyclerCheckout
                );

        recyclerCheckout.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        CheckoutAdapter checkoutAdapter =
                new CheckoutAdapter();

        recyclerCheckout.setAdapter(
                checkoutAdapter
        );

        viewModel.getItems().observe(
                getViewLifecycleOwner(),
                items -> {

                    if (items == null) {

                        currentCartItems =
                                new ArrayList<>();

                        checkoutAdapter.submitList(
                                new ArrayList<>()
                        );

                        return;
                    }

                    currentCartItems =
                            new ArrayList<>(items);

                    checkoutAdapter.submitList(
                            new ArrayList<>(items)
                    );
                }
        );
    }


    private void observeSubtotal() {

        viewModel.getSubtotal().observe(
                getViewLifecycleOwner(),
                value -> {

                    subtotal =
                            value != null
                                    ? value
                                    : 0L;

                    /*
                     * Este cálculo es solamente para mostrar
                     * una estimación al usuario.
                     *
                     * NO se utiliza para crear la orden
                     * como valor confiable.
                     */
                    total =
                            subtotal + delivery;

                    updateTotalUI();
                }
        );
    }


    private void updateTotalUI() {

        Subtotal.setText(
                "Subtotal: $" +
                        formatMoney(subtotal)
        );

        deliveryfree.setText(
                "Delivery: $" +
                        formatMoney(delivery)
        );

        Total.setText(
                "Total: $" +
                        formatMoney(total)
        );

        btnPlaceOrder.setText(
                "Place Order - $" +
                        formatMoney(total)
        );
    }


    private String formatMoney(long cents) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                cents / 100.0
        );
    }


    private boolean validateForm() {

        if (inputName.getText()
                .toString()
                .trim()
                .isEmpty()) {

            inputName.setError("Required");
            return false;
        }

        if (inputEmail.getText()
                .toString()
                .trim()
                .isEmpty()) {

            inputEmail.setError("Required");
            return false;
        }

        if (inputPhone.getText()
                .toString()
                .trim()
                .isEmpty()) {

            inputPhone.setError("Required");
            return false;
        }

        if (inputAddress.getText()
                .toString()
                .trim()
                .isEmpty()) {

            inputAddress.setError("Required");
            return false;
        }

        if (inputDate.getText()
                .toString()
                .trim()
                .isEmpty()) {

            inputDate.setError("Required");
            return false;
        }

        if (inputTime.getText()
                .toString()
                .trim()
                .isEmpty()) {

            inputTime.setError("Required");
            return false;
        }

        int selectedPayment =
                paymentGroup.getCheckedRadioButtonId();

        if (selectedPayment == -1) {

            Toast.makeText(
                    requireContext(),
                    "Select payment method",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }



    private void initView(View view) {

        inputName =
                view.findViewById(
                        R.id.inputName
                );

        inputEmail =
                view.findViewById(
                        R.id.inputEmail
                );

        inputPhone =
                view.findViewById(
                        R.id.inputPhone
                );

        inputAddress =
                view.findViewById(
                        R.id.inputAddress
                );

        Subtotal =
                view.findViewById(
                        R.id.subtotalText
                );

        deliveryfree =
                view.findViewById(
                        R.id.deliveryFeeText
                );

        Total =
                view.findViewById(
                        R.id.totalText
                );

        paymentGroup =
                view.findViewById(
                        R.id.paymentGroup
                );

        btnPlaceOrder =
                view.findViewById(
                        R.id.btnPlaceOrder
                );

        inputDate =
                view.findViewById(
                        R.id.inputDate
                );

        inputTime =
                view.findViewById(
                        R.id.inputTime
                );

    }
    private void createOrder() {

        if (currentCartItems == null ||
                currentCartItems.isEmpty()) {

            if (isAdded()) {

                Toast.makeText(
                        requireContext(),
                        "Your cart is empty",
                        Toast.LENGTH_SHORT
                ).show();
            }

            return;
        }

        CreateOrderRequest request =
                buildCreateOrderRequest();

        Profile profile =
                buildProfile();

        viewModel.placeOrder(
                request,
                profile
        );
    }




    private CreateOrderRequest buildCreateOrderRequest() {

        List<OrderItemRequest> items =
                new ArrayList<>(
                        currentCartItems.size()
                );

        for (CartItem cartItem :
                currentCartItems) {

            items.add(
                    new OrderItemRequest(
                            cartItem.getProductId(),
                            cartItem.getQuantity()
                    )
            );
        }

        return new CreateOrderRequest(

                inputName.getText()
                        .toString()
                        .trim(),

                inputEmail.getText()
                        .toString()
                        .trim(),

                inputPhone.getText()
                        .toString()
                        .trim(),

                inputAddress.getText()
                        .toString()
                        .trim(),

                getPaymentMethod(),

                inputDate.getText()
                        .toString()
                        .trim(),

                inputTime.getText()
                        .toString()
                        .trim(),

                items
        );
    }



    private String getPaymentMethod() {

        int checkedId =
                paymentGroup.getCheckedRadioButtonId();

        RadioButton radio =
                paymentGroup.findViewById(
                        checkedId
                );

        return radio.getText()
                .toString()
                .trim();
    }

    private Profile buildProfile() {

        return new Profile(
                null,

                inputName.getText()
                        .toString()
                        .trim(),

                inputEmail.getText()
                        .toString()
                        .trim(),

                inputPhone.getText()
                        .toString()
                        .trim(),

                inputAddress.getText()
                        .toString()
                        .trim()
        );
    }



    private void navigateToOrders() {

        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.frame_layout,
                        new ClientOrderStatusFragment()
                )
                .addToBackStack(null)
                .commit();
    }
}
