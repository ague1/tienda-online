package com.example.myapplication.user_auth.resetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class OtpActivity extends AppCompatActivity {
    EditText code1, code2, code3, code4;
    Button buttonverify;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forget_code);

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        buttonverify = findViewById(R.id.verifyButton);
        if (buttonverify == null) {
            Log.e("DEBUG", "Botón verifyButton es null! Revisa setContentView y el ID");
        } else {
            Log.d("DEBUG", "Botón verifyButton encontrado correctamente");
        }

        email = getIntent().getStringExtra("email");

        setupOtpInputs();

        buttonverify.setOnClickListener(v -> {
            String otp = code1.getText().toString()
                    + code2.getText().toString()
                    + code3.getText().toString()
                    + code4.getText().toString();

            if (otp.length() != 4) {
                Toast.makeText(this, "Enter complete code", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyOTP(email, otp);
        });
    }

    private void setupOtpInputs() {
        code1.addTextChangedListener(new OtpTextWatcher(code1, code2));
        code2.addTextChangedListener(new OtpTextWatcher(code2, code3));
        code3.addTextChangedListener(new OtpTextWatcher(code3, code4));
        code4.addTextChangedListener(new OtpTextWatcher(code4, null));
    }

    private void verifyOTP(String email, String enteredOTP) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("password_otps").document(email)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String otp = document.getString("otp");
                        long expiration = document.getLong("expiration");

                        if (System.currentTimeMillis() > expiration) {
                            Toast.makeText(this, "El código OTP ha expirado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (otp.equals(enteredOTP)) {
                            // OTP correcto → ir a pantalla de restablecer contraseña
                            Intent intent = new Intent(this, ResetPassword.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish(); // opcional: cerrar pantalla de OTP
                        } else {
                            Toast.makeText(this, "Código OTP incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No se encontró el código OTP", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
