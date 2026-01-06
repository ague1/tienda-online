package com.example.myapplication.pedidos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DeliveryOrderActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class pedidoAdapter extends RecyclerView.Adapter<pedidoAdapter.ViewHolder> {
    private final Context context;
    private List<DocumentSnapshot> orders;

    public pedidoAdapter(Context context, List<DocumentSnapshot> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void updateOrders(List<DocumentSnapshot> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public pedidoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_pedidos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pedidoAdapter.ViewHolder holder, int position) {
        DocumentSnapshot doc = orders.get(position);

        holder.itemView.setOnClickListener(v -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(docUser -> {

                        String role = docUser.getString("role");

                        if (role == null) role = "users";

                        if (role.equals("delivery")) {

                            // ABRIR PANTALLA DE CAMBIO DE STATUS (BOTONES)
                            Intent i = new Intent(context, DeliveryOrderActivity.class);
                            i.putExtra("orderId", doc.getId());
                            context.startActivity(i);

                        } else {

                            // USUARIO NORMAL → TIMELINE
                            Intent i = new Intent(context, OrderProcessActivity.class);
                            i.putExtra("orderId", doc.getId());
                            context.startActivity(i);
                        }
                    });
        });
        holder.txtOrderId.setText("Orden: " + doc.getId());
        String status = doc.getString("status");
        holder.txtOrderStatus.setText(status != null ? status : "—");

        Double total = doc.getDouble("total");
        holder.txtOrderTotal.setText(total != null ? "$" + String.format(Locale.getDefault(), "%.2f", total) : "$0.00");

        if (doc.getDate("timestamp") != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(doc.getDate("timestamp"));
            holder.txtOrderDate.setText(date);
        } else holder.txtOrderDate.setText("");

    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderDate, txtOrderTotal, txtOrderStatus;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtOrderTotal = itemView.findViewById(R.id.txtOrderTotal);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
        }
    }
}

