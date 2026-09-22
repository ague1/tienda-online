package com.example.myapplication.features.order.presentation.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

import android.widget.TextView;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.features.order.presentation.viewmodel.OrderProcessViewModel;
import com.example.myapplication.features.order.domain.model.OrderTimeline;
import com.example.myapplication.features.order.domain.model.Order;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;



@AndroidEntryPoint
public class OrderProcessActivity extends AppCompatActivity {

    TextView txtOrderId, txtTotal;

    View dotPlaced;
    View dotPending;
    View dotConfirmed;
    View dotProcessing;
    View dotDelivered;

    private OrderProcessViewModel viewModel;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_order_process
        );

        txtOrderId =
                findViewById(
                        R.id.txtDetailOrderId
                );

        txtTotal =
                findViewById(
                        R.id.txtDetailTotal
                );

        dotPlaced =
                findViewById(
                        R.id.dot_placed
                );

        dotPending =
                findViewById(
                        R.id.dot_pending
                );

        dotConfirmed =
                findViewById(
                        R.id.dot_confirmed
                );

        dotProcessing =
                findViewById(
                        R.id.dot_processing
                );

        dotDelivered = findViewById(
                        R.id.dot_delivered
                );

        viewModel = new ViewModelProvider(this)
                        .get(OrderProcessViewModel.class);

        observeOrder();

        String orderId =
                getIntent().getStringExtra(
                        "orderId"
                );

        if (orderId != null) {

            txtOrderId.setText(
                    "Orden: " + orderId
            );

            viewModel.listenOrder(
                    orderId
            );
        }
    }

    private void observeOrder() {

        viewModel.getState().observe(
                this,
                state -> {

                    if (state == null) {
                        return;
                    }

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:
                            break;

                        case SUCCESS:

                            Order order =
                                    state.getData();

                            if (order == null) {
                                return;
                            }

                            txtTotal.setText(
                                    String.format(
                                            Locale.getDefault(),
                                            "$%.2f",
                                            order.getTotal() / 100.0
                                    )
                            );

                            updateTimeline(
                                    order.getTimeline()
                            );

                            break;

                        case ERROR:
                            /*
                             * Conservamos por ahora
                             * el comportamiento actual:
                             * no mostramos un mensaje nuevo.
                             *
                             * La centralización de errores
                             * la veremos en Fase 4.
                             */
                            break;
                    }
                }
        );
    }

    private void updateTimeline(
            OrderTimeline timeline
    ) {

        if (timeline == null) {

            setDotActive(
                    dotPlaced,
                    false
            );

            setDotActive(
                    dotPending,
                    false
            );

            setDotActive(
                    dotConfirmed,
                    false
            );

            setDotActive(
                    dotProcessing,
                    false
            );

            setDotActive(
                    dotDelivered,
                    false
            );

            return;
        }

        setDotActive(
                dotPlaced,
                timeline.getPlaced() != null
        );

        setDotActive(
                dotPending,
                timeline.getPending() != null
        );

        setDotActive(
                dotConfirmed,
                timeline.getConfirmed() != null
        );

        setDotActive(
                dotProcessing,
                timeline.getProcessing() != null
        );

        setDotActive(
                dotDelivered,
                timeline.getDelivered() != null
        );
    }

    private void setDotActive(
            View dot,
            boolean active
    ) {

        if (active) {
            dot.setBackgroundResource(
                    R.drawable.circle_black
            );
        } else {
            dot.setBackgroundResource(
                    R.drawable.circle_gray
            );
        }
    }
}

