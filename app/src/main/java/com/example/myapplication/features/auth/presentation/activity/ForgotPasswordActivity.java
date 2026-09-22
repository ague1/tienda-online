package com.example.myapplication.features.auth.presentation.activity;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.presentation.viewmodel.OtpViewModel;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailLogin;
    private Button buttonResetPassword;

    private OtpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        initViews();
        initViewModel();
        observeState();
        setupListeners();
    }

    private void initViews() {

        emailLogin =
                findViewById(R.id.email_login);

        buttonResetPassword =
                findViewById(R.id.button_resetPassword);
    }

    private void initViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(OtpViewModel.class);
    }

    private void observeState() {

        viewModel
                .getSendOtpState()
                .observe(this, state -> {

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:

                            setLoading(true);

                            break;

                        case SUCCESS:

                            setLoading(false);

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "OTP enviado al correo",
                                    Toast.LENGTH_SHORT
                            ).show();

                            String email =
                                    emailLogin.getText()
                                            .toString()
                                            .trim();

                            Intent intent =
                                    new Intent(
                                            ForgotPasswordActivity.this,
                                            OtpActivity.class
                                    );

                            intent.putExtra(
                                    "email",
                                    email
                            );

                            startActivity(intent);
                            finish();

                            break;

                        case ERROR:

                            setLoading(false);

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    state.getError() != null
                                            ? state.getError()
                                            : "No se pudo enviar el código. Intente nuevamente.",
                                    Toast.LENGTH_LONG
                            ).show();

                            break;
                    }
                });
    }

    private void setupListeners() {

        buttonResetPassword.setOnClickListener(
                v -> sendOtp()
        );
    }

    private void sendOtp() {

        if (!validateEmail()) {
            return;
        }

        String email =
                emailLogin.getText()
                        .toString()
                        .trim();

        viewModel.sendOtp(email);
    }

    private boolean validateEmail() {

        String email =
                emailLogin.getText()
                        .toString()
                        .trim();

        if (email.isEmpty()) {

            emailLogin.setError(
                    "Ingrese su correo"
            );

            emailLogin.requestFocus();

            return false;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            emailLogin.setError(
                    "Correo inválido"
            );

            emailLogin.requestFocus();

            return false;
        }

        emailLogin.setError(null);

        return true;
    }

    private void setLoading(boolean loading) {

        buttonResetPassword.setEnabled(!loading);

        if (loading) {

            buttonResetPassword.setText(
                    "Enviando..."
            );

        } else {

            buttonResetPassword.setText("Enviar código");
        }
    }
}
