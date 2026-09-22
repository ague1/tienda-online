package com.example.myapplication.features.auth.presentation.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.myapplication.R;
import com.example.myapplication.features.auth.presentation.viewmodel.ResetPasswordViewModel;
import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText newPassword;
    private TextInputEditText confirmPassword;
    private Button resetPasswordButton;

    private String email;
    private String resetToken;
    private String challengeId;

    private ResetPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forget_new_password);

        initUI();
        initViewModel();
        observeState();
        initListeners();
    }

    private void initUI() {

        newPassword =
                findViewById(R.id.inputNewPassword);

        confirmPassword =
                findViewById(R.id.inputConfirmPassword);

        resetPasswordButton =
                findViewById(R.id.resetPasswordButton);

        email =
                getIntent().getStringExtra("email");

        resetToken =
                getIntent().getStringExtra("resetToken");

        challengeId =
                getIntent().getStringExtra("challengeId");
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(this)
                .get(ResetPasswordViewModel.class);
    }

    private void initListeners() {

        resetPasswordButton.setOnClickListener(
                v -> validateAndResetPassword()
        );
    }

    private void observeState() {

        viewModel.getResetPasswordState()
                .observe(this, state -> {

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:

                            resetPasswordButton.setEnabled(false);

                            break;

                        case SUCCESS:

                            resetPasswordButton.setEnabled(true);

                            Toast.makeText(
                                    ResetPasswordActivity.this,
                                    "Contraseña actualizada correctamente",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                            break;

                        case ERROR:

                            resetPasswordButton.setEnabled(true);

                            Toast.makeText(
                                    ResetPasswordActivity.this,
                                    state.getError(),
                                    Toast.LENGTH_LONG
                            ).show();

                            break;
                    }
                });
    }

    private void validateAndResetPassword() {

        String newPasswordText =
                newPassword.getText().toString();

        String confirmPasswordText =
                confirmPassword.getText().toString();

        if (newPasswordText.length() < 8) {

            newPassword.setError(
                    "La contraseña debe tener al menos 8 caracteres"
            );

            return;
        }

        if (newPasswordText.length() > 128) {

            newPassword.setError(
                    "La contraseña no puede superar 128 caracteres"
            );

            return;
        }

        if (!newPasswordText.matches(".*[A-Z].*")) {

            newPassword.setError(
                    "Incluye al menos una MAYÚSCULA"
            );

            return;
        }

        if (!newPasswordText.matches(".*[0-9].*")) {

            newPassword.setError(
                    "Incluye al menos un número"
            );

            return;
        }

        if (!newPasswordText.equals(confirmPasswordText)) {

            confirmPassword.setError(
                    "Las contraseñas no coinciden"
            );

            return;
        }

        viewModel.resetPassword(
                challengeId,
                resetToken,
                newPasswordText
        );
    }
}