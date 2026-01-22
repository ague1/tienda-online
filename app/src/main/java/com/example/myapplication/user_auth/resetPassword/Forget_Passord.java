package com.example.myapplication.user_auth.resetPassword;

import android.content.Intent;
import android.os.Bundle;
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


import org.json.JSONObject;

import java.io.IOException;
import com.example.myapplication.BuildConfig;
import com.example.network.ApiEndpoints;

import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(ApiEndpoints.SEND_OTP)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Forget_Passord.this,
                                "Error de conexión con servidor",
                                Toast.LENGTH_LONG).show()
                );
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                runOnUiThread(() -> {
                    Toast.makeText(Forget_Passord.this,
                            "OTP enviado al correo",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Forget_Passord.this, OtpActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                });
            }
        });
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