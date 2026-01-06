package com.example.myapplication.pedidos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

import android.widget.TextView;
import android.view.View;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderProcessActivity extends AppCompatActivity {

    TextView txtOrderId, txtTotal;
    View dotPlaced, dotPending, dotConfirmed, dotProcessing, dotDelivered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_process);

        txtOrderId = findViewById(R.id.txtDetailOrderId);
        txtTotal = findViewById(R.id.txtDetailTotal);

        dotPlaced = findViewById(R.id.dot_placed);
        dotPending = findViewById(R.id.dot_pending);
        dotConfirmed = findViewById(R.id.dot_confirmed);
        dotProcessing = findViewById(R.id.dot_processing);
        dotDelivered = findViewById(R.id.dot_delivered);

        String orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            txtOrderId.setText("Orden: " + orderId);
            listenOrder(orderId);
        }
    }

    private void listenOrder(String orderId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(orderId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    Double total = snapshot.getDouble("total");
                    txtTotal.setText(total != null ? "$" + String.format("%.2f", total) : "$0.00");

                    // timeline map
                    // Si guardaste timeline con campos placed/pending/confirmed/processing/delivered (Date o null)
                    Object placed = snapshot.get("timeline.placed");
                    Object pending = snapshot.get("timeline.pending");
                    Object confirmed = snapshot.get("timeline.confirmed");
                    Object processing = snapshot.get("timeline.processing");
                    Object delivered = snapshot.get("timeline.delivered");

                    // activar pasos según existencia de timestamp
                    setDotActive(dotPlaced, placed != null);
                    setDotActive(dotPending, pending != null);
                    setDotActive(dotConfirmed, confirmed != null);
                    setDotActive(dotProcessing, processing != null);
                    setDotActive(dotDelivered, delivered != null);
                });
    }

    private void setDotActive(View dot, boolean active) {
        if (active) dot.setBackgroundResource(R.drawable.circle_black);
        else dot.setBackgroundResource(R.drawable.circle_gray);
    }

}