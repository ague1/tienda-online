package com.example.myapplication.features.order.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.order.presentation.activity.OrderProcessActivity;
import com.example.myapplication.features.order.domain.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private final Context context;
    private List<Order> orders;

    public OrderListAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_pedidos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderProcessActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });

        holder.txtOrderId.setText("Orden: " + order.getId());
        holder.txtOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "—");
        holder.txtOrderTotal.setText(
                "$" + String.format(
                        Locale.getDefault(),
                        "%.2f",
                        order.getTotal() / 100.0
                )

        );

        if (order.getTimestamp() !=  null) {
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(order.getTimestamp());
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

