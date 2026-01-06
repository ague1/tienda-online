package com.example.myapplication.user_auth.resetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Forget_Passord extends AppCompatActivity {

    EditText  email_login;
    Button button_resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email_login = findViewById(R.id.email_login);
        button_resetPassword = findViewById(R.id.button_resetPassword);


        button_resetPassword.setOnClickListener(v -> {
            String email = email_login.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
               email_login.setError("Correo inválido");
                return;
            }
            sendOTPToEmail(email);
        });
    }

    private void sendOTPToEmail(String email) {
        String otp = generateOTP(6); // función abajo
        long expiration = System.currentTimeMillis() + 10 * 60 * 1000; // 10 minutos

        // Guardar OTP en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        data.put("expiration", expiration);

        db.collection("password_otps").document(email)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "OTP enviado al correo", Toast.LENGTH_SHORT).show();
                    // Aquí se enviaría el OTP por correo usando Cloud Function
                    Intent intent = new Intent(this, OtpActivity.class);
                    intent.putExtra("email", email); // Pasamos el email para validar después
                    startActivity(intent);

                    // Opcional: cerrar la pantalla actual para que el usuario no pueda volver
                    finish();

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Generar OTP
    private String generateOTP(int length) {
        String chars = "0123456789";
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            otp.append(chars.charAt(random.nextInt(chars.length())));
        }
        return otp.toString();
    }
}