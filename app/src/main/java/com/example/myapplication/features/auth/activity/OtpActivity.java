package com.example.myapplication.features.auth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.repository.OtpCallback;
import com.example.myapplication.features.auth.usecase.VerifyOtpUseCase;
import com.example.myapplication.shared.OtpTextWatcher;

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
public class OtpActivity extends AppCompatActivity {
    @Inject
    VerifyOtpUseCase verifyOtpUseCase;
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

    private void verifyOTP(String email, String otp) {
        verifyOtpUseCase.execute(email, otp, new OtpCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Intent intent = new Intent(
                            OtpActivity.this,
                            ResetPasswordActivity.class
                    );

                    intent.putExtra(
                            "email",
                            email
                    );
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(
                        OtpActivity.this,
                                message,
                                Toast.LENGTH_SHORT)
                        .show()
                );
            }
        });
    }
    private void setupOtpInputs() {
        code1.addTextChangedListener(new OtpTextWatcher(code1, code2, null));
        code2.addTextChangedListener(new OtpTextWatcher(code2, code3, code1));
        code3.addTextChangedListener(new OtpTextWatcher(code3, code4, code2));
        code4.addTextChangedListener(new OtpTextWatcher(code4, null, code3));
    }

}