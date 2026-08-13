package com.example.myapplication.features.auth.activity;
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
import com.example.myapplication.features.auth.repository.AuthRepository;
import com.example.myapplication.features.auth.usecase.SendOtpUseCase;
import com.example.network.ApiEndpoints;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@AndroidEntryPoint
public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email_login;
    Button button_resetPassword;

    @Inject
    SendOtpUseCase sendOtpUseCase;

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
            if (!validateEmail()) {
                return;
            }
            String email = email_login.getText().toString().trim();

            sendOtpUseCase.execute(email, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this,
                            "Error de conexión con servidor",
                            Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String result = response.body() != null
                            ? response.body().string()
                            : "";

                    runOnUiThread(() -> {

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "OTP enviado al correo",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    ForgotPasswordActivity.this,
                                    OtpActivity.class
                            );

                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    result,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
                }
            });

        });
    }

    private boolean validateEmail() {
        String email = email_login.getText().toString().trim();

        if (email.isEmpty()) {
            email_login.setError("Ingrese su correo");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_login.setError("Correo inválido");
            return false;
        }

        return true;
    }
}