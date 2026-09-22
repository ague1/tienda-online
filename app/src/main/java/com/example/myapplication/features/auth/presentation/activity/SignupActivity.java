package com.example.myapplication.features.auth.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.presentation.viewmodel.SignupViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignupActivity extends AppCompatActivity {

    private EditText editTextUser, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;
    private TextView linkLogin;
    private SignupViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singup);

        initUI();
        initListeners();

        viewModel.getRegisterState()
                .observe(this, state -> {

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:

                            buttonSignup.setEnabled(false);

                            break;

                        case SUCCESS:

                            buttonSignup.setEnabled(true);

                            Toast.makeText(
                                    SignupActivity.this,
                                    "Cuenta creada con éxito ✔",
                                    Toast.LENGTH_LONG
                            ).show();

                            clearInputs();

                            startActivity(
                                    new Intent(
                                            SignupActivity.this,
                                            LoginActivity.class
                                    )
                            );

                            finish();

                            break;

                        case ERROR:

                            buttonSignup.setEnabled(true);

                            String error =
                                    state.getError();

                            if (
                                    "EMAIL_ALREADY_EXISTS"
                                            .equals(error)
                            ) {

                                editTextEmail.setError(
                                        "Este correo ya está registrado"
                                );

                            } else {

                                Toast.makeText(
                                        SignupActivity.this,
                                        "Error al registrar",
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            break;
                    }
                });

    }
    private void initUI() {
        editTextUser = findViewById(R.id.name_user);
        editTextEmail = findViewById(R.id.email_sigup);
        editTextPassword = findViewById(R.id.password_sigup);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonSignup = findViewById(R.id.button_singup);
        linkLogin = findViewById(R.id.link_loging);
        viewModel = new ViewModelProvider(this)
                .get(SignupViewModel.class);

    }

    private void initListeners() {

        linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        buttonSignup.setOnClickListener(v -> startSignup());
    }

    private void startSignup() {

        String name = editTextUser.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String pass = editTextPassword.getText().toString().trim();
        String confirmPass = editTextConfirmPassword.getText().toString().trim();

        // VALIDACIONES
        if (!validateInputs(name, email, pass, confirmPass)) return;


    }
    private boolean validateInputs(String name, String email, String pass, String confirmPass) {

        if (name.isEmpty()) {
            editTextUser.setError("Ingresa tu nombre");
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Ingresa tu correo");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Correo no válido");
            return false;
        }

        if (pass.isEmpty()) {
            editTextPassword.setError("Ingresa una contraseña");
            return false;
        }

        if (pass.length() < 8) {

            editTextPassword.setError(
                    "La contraseña debe tener al menos 8 caracteres"
            );

            return false;
        }

        if (pass.length() > 128) {

            editTextPassword.setError(
                    "La contraseña no puede superar 128 caracteres"
            );

            return false;
        }

        if (!pass.matches(".*[A-Z].*")) {
            editTextPassword.setError("Incluye al menos una MAYÚSCULA");
            return false;
        }

        if (!pass.matches(".*[0-9].*")) {
            editTextPassword.setError("Incluye al menos un número");
            return false;
        }

        if (!pass.equals(confirmPass)) {
            editTextConfirmPassword.setError("Las contraseñas no coinciden");
            return false;
        }

        return true;
    }
    private void clearInputs() {
        editTextUser.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
    }
}