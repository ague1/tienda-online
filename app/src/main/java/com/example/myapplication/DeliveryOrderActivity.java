package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.pedidos.Pedido_Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryOrderActivity extends AppCompatActivity {

    ImageView imgQr;
    TextView txtCustomerName, txtAddress, txtPhone, txtItems, txtTotal;
    Spinner spinnerStatus;
    Button btnNavigate, btnUpdate;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String orderId,userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_order);

        imgQr = findViewById(R.id.imgQr);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtItems = findViewById(R.id.txtItems);
        txtTotal = findViewById(R.id.txtTotal);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnNavigate = findViewById(R.id.btnNavigate);

        orderId = getIntent().getStringExtra("orderId");

        loadUserRole();
        loadOrderDetails();

        btnUpdate.setOnClickListener(v -> updateStatus());
    }

    private void loadUserRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {

                    userRole = doc.getString("role");

                    if (userRole == null ||
                            !(userRole.equals("admin") || userRole.equals("delivery"))) {
                        Toast.makeText(this, "Sin permisos", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                });
    }


    private void loadOrderDetails() {

        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String userId = doc.getString("userId");
                    Double total = doc.getDouble("total");

                    txtTotal.setText("Total: $" + String.format(Locale.getDefault(), "%.2f", total));

                    List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");

                    if (items != null) {
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, Object> item : items) {
                            sb.append("• ")
                                    .append(item.get("nombre"))       // nombre correcto en Firestore
                                    .append(" (x")
                                    .append(item.get("cantidad"))     // cantidad correcta en Firestore
                                    .append(") - $")
                                    .append(item.get("precio"))       // opcional
                                    .append("\n");
                        }
                        txtItems.setText(sb.toString());
                    }

                    // cargar estado
                    String status = doc.getString("status");
                    String[] estados = {"pending", "processing", "confirmed", "delivered", "cancelled"};

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, estados);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerStatus.setAdapter(adapter);

                    for (int i = 0; i < estados.length; i++) {
                        if (estados[i].equals(status)) {
                            spinnerStatus.setSelection(i);
                        }
                    }

                    generateQR(orderId);

                    if (userId != null) loadUserInfo(userId);
                });
    }

    // -------------------------
    // Datos del cliente
    // -------------------------
    private void loadUserInfo(String userId) {

        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {

                    txtCustomerName.setText(doc.getString("name"));
                    txtAddress.setText("Dirección: " + doc.getString("address"));
                    txtPhone.setText("Tel: " + doc.getString("phone"));

                    String address = doc.getString("address");

                    btnNavigate.setOnClickListener(v -> openMaps(address));
                });
    }

    // -------------------------
    // Actualizar estado
    // -------------------------
    private void updateStatus() {

        if (!userRole.equals("admin") && !userRole.equals("delivery")) {
            Toast.makeText(this, "No autorizado", Toast.LENGTH_SHORT).show();
            return;
        }

        String newStatus = spinnerStatus.getSelectedItem().toString();

        db.collection("orders").document(orderId)
                .update(
                        "status", newStatus,
                        "timeline." + newStatus, new Date()
                )
                .addOnSuccessListener(v -> {

                    Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();

                    // REGRESAR A LA LISTA DE PEDIDOS
                    Intent intent = new Intent(DeliveryOrderActivity.this, Pedido_Fragment.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                );
    }
    // -------------------------
    // Generar QR
    // -------------------------
    private void generateQR(String data) {

        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = new BarcodeEncoder().createBitmap(matrix);
            imgQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // Abrir mapas
    // -------------------------
    private void openMaps(String address) {
        if (address == null || address.isEmpty()) {
            Toast.makeText(this, "Dirección no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
