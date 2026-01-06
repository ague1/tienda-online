package com.example.myapplication.user_auth.resetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Collections;

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

        email_login= findViewById(R.id.email_login);
        button_resetPassword = findViewById(R.id.button_resetPassword);

        button_resetPassword.setOnClickListener(v->{
            String email = email_login.getText().toString().trim();

            if (!email.isEmpty()){
                sendOtp(email);
            }
        });
    }

    private void sendOtp(String email) {
        FirebaseFunctions.getInstance()
                .getHttpsCallable("sendOtpReset")
                .call(Collections.singletonMap("email", email))
                .addOnSuccessListener(result -> {
                    Intent intent = new Intent(this, OtpActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                })
                .addOnFailureListener(e ->{
                            Log.e("OTP_ERROR", e.getMessage(), e);
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
                       // Toast.makeText(this, "Error sending code", Toast.LENGTH_SHORT).show()
                );
    }
}