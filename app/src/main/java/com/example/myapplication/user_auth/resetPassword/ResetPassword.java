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
            String pass1 = newPassword.getText().toString().trim();
            String pass2 = confirmPassword.getText().toString().trim();

            if (pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass1.equals(pass2)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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
