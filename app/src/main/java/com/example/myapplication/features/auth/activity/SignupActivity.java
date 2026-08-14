package com.example.myapplication.features.auth.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.features.auth.usecase.RegisterUseCase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignupActivity extends AppCompatActivity {

    private EditText editTextUser, editTextEmail, editTextPassword, editTextConfirmPassword;
    private RadioGroup radioUserType;
    private Button buttonSignup;
    private TextView linkLogin;
    private ProgressDialog loadingDialog;
    @Inject
    RegisterUseCase registerUseCase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singup);

        initUI();
        initListeners();
    }
    private void initUI() {
        editTextUser = findViewById(R.id.name_user);
        editTextEmail = findViewById(R.id.email_sigup);
        editTextPassword = findViewById(R.id.password_sigup);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonSignup = findViewById(R.id.button_singup);
        linkLogin = findViewById(R.id.link_loging);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Creando cuenta...");
        loadingDialog.setCancelable(false);
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

        int selectedId = radioUserType.getCheckedRadioButtonId();
        String userType = ((RadioButton) findViewById(selectedId)).getText().toString();

        loadingDialog.show();

        createUser(name, email, pass, userType);

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

        if (pass.length() < 6) {
            editTextPassword.setError("La contraseña debe tener 6 caracteres");
            return false;
        }

        // Contraseña fuerte opcional
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
    private void createUser(String name, String email, String pass, String userType) {

        registerUseCase.execute(name, email, pass)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();

                    Toast.makeText(
                            this,
                            "Cuenta creada con éxito ✔",
                            Toast.LENGTH_LONG
                    ).show();
                    clearInputs();

                    startActivity(
                            new Intent(this, LoginActivity.class)
                    );

                    finish();

                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    if(e.getMessage().equals("EMAIL_ALREADY_EXISTS")){

                        editTextEmail.setError(
                                "Este correo ya está registrado"
                        );

                    } else {

                        Toast.makeText(
                                this,
                                "Error al registrar",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
    private void clearInputs() {
        editTextUser.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
    }
}