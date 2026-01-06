package com.example.myapplication.user_auth.resetPassword;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    TextInputEditText newPassword,confirmPassword;
    Button resetPasswordButton;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forget_new_password);

        newPassword = findViewById(R.id.inputNewPassword);
        confirmPassword = findViewById(R.id.inputConfirmPassword);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        email = getIntent().getStringExtra("email");

        resetPasswordButton.setOnClickListener(v -> {
            String newPasswordText = newPassword.getText().toString().trim();
            String confirmPasswordText= confirmPassword.getText().toString().trim();

            // 4️⃣ Validaciones
            if (newPassword.length() < 6) {
                newPassword.setError("La contraseña debe tener al menos 6 caracteres");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                confirmPassword.setError("Las contraseñas no coinciden");
                return;
            }

            sendResetEmail();
        });
    }

    private void sendResetEmail() {
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Reset email sent. Check your inbox.",
                            Toast.LENGTH_LONG).show();

                    finish(); // volver al login
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error sending reset email",
                                Toast.LENGTH_SHORT).show()
                );
    }
}
