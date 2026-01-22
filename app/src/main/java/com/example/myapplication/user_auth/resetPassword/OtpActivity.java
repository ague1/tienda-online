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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        code1.addTextChangedListener(new OtpTextWatcher(code1, code2, null));
        code2.addTextChangedListener(new OtpTextWatcher(code2, code3, code1));
        code3.addTextChangedListener(new OtpTextWatcher(code3, code4, code2));
        code4.addTextChangedListener(new OtpTextWatcher(code4, null, code3));
    }

    private void verifyOTP(String email, String enteredOTP) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("otp", enteredOTP);

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:3000/verify-otp") // emulador
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(OtpActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(res);
                        boolean valid = obj.getBoolean("valid");

                        runOnUiThread(() -> {
                            if (valid) {
                                Intent intent = new Intent(OtpActivity.this, ResetPassword.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(OtpActivity.this, "Código incorrecto o expirado", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(OtpActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error interno", Toast.LENGTH_SHORT).show();
        }
    }
}