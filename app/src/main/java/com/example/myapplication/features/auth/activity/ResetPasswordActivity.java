package com.example.myapplication.features.auth.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.R;
import com.example.myapplication.features.auth.usecase.ResetPasswordUseCase;
import com.example.network.ApiEndpoints;
import com.google.android.material.textfield.TextInputEditText;

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
public class ResetPasswordActivity extends AppCompatActivity {

    TextInputEditText newPassword, confirmPassword;
    Button resetPasswordButton;
    String email;

    @Inject
    ResetPasswordUseCase resetPasswordUseCase;

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

            String newPasswordText =
                    newPassword.getText().toString().trim();

            String confirmPasswordText =
                    confirmPassword.getText().toString().trim();


            if (newPasswordText.length() < 6) {

                newPassword.setError(
                        "La contraseña debe tener al menos 6 caracteres"
                );

                return;
            }


            if (!newPasswordText.equals(confirmPasswordText)) {

                confirmPassword.setError(
                        "Las contraseñas no coinciden"
                );

                return;
            }
            resetPassword(newPasswordText);

        });
    }


    private void resetPassword(String password) {
        resetPasswordUseCase.execute(email, password, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(
                        ResetPasswordActivity.this,
                                "Error de conexión con servidor",
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body() != null
                        ? response.body().string() : "";

                runOnUiThread(() -> {

                    if (response.isSuccessful()) {
                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "Contraseña actualizada correctamente",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();

                    } else {

                        Toast.makeText(
                                ResetPasswordActivity.this,
                                result,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }
        });
    }
}