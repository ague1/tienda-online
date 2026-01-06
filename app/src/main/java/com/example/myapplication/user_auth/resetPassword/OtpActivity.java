package com.example.myapplication.user_auth.resetPassword;

import android.content.Intent;
import android.os.Bundle;
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

        // Email recibido de Forget_Password
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

            verifyOtp(email, otp);
        });
    }

    private void setupOtpInputs() {
        code1.addTextChangedListener(new OtpTextWatcher(code1, code2));
        code2.addTextChangedListener(new OtpTextWatcher(code2, code3));
        code3.addTextChangedListener(new OtpTextWatcher(code3, code4));
        code4.addTextChangedListener(new OtpTextWatcher(code4, null));
    }

    private void verifyOtp(String email, String otp) {
        FirebaseFirestore.getInstance()
                .collection("password_resets")
                .document(email)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this, "Code not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String savedCode = snapshot.getString("code");
                    long expiresAt = snapshot.getLong("expiresAt");

                    if (System.currentTimeMillis() > expiresAt) {
                        Toast.makeText(this, "Code expired", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (otp.equals(savedCode)) {
                        Toast.makeText(this, "Code verified", Toast.LENGTH_SHORT).show();

                        // Aquí pasas a reset password
                        Intent intent = new Intent(this, ResetPassword.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error verifying code", Toast.LENGTH_SHORT).show()
                );
    }
}
