package com.example.myapplication.features.auth.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.presentation.viewmodel.LoginViewModel;
import com.example.myapplication.features.auth.application.usecase.AuthUseCase;
import com.example.myapplication.features.main.presentation.MainActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonMain;
    private TextView linkForgotPassword;
    private TextView linkSingup;
    private LoginViewModel viewModel;
    @Inject
    AuthUseCase authUseCase;

    @Override
    protected void onStart() {
        super.onStart();

        if (authUseCase.isLogged()) {

            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
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

        editTextEmail = findViewById(R.id.email_login);

        editTextPassword = findViewById(R.id.password_login);

        linkForgotPassword = findViewById(R.id.link_forgetPassword);

        linkSingup = findViewById(R.id.link_singup);

        buttonMain = findViewById(R.id.button_login);

        viewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);


        linkSingup.setOnClickListener(v -> {
            Intent intent = new Intent(
                LoginActivity.this, SignupActivity.class
            );

            startActivity(intent);
        });


        linkForgotPassword.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            ForgotPasswordActivity.class
                    );

            startActivity(intent);
        });


        buttonMain.setOnClickListener(v -> {

            String email =
                    editTextEmail
                            .getText()
                            .toString()
                            .trim();

            String password =
                    editTextPassword
                            .getText()
                            .toString();

            if (email.isEmpty()) {

                Toast.makeText(
                        LoginActivity.this,
                        "Ingrese su email",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (password.isEmpty()) {

                Toast.makeText(
                        LoginActivity.this,
                        "Ingrese su contraseña",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            viewModel.login(
                    email,
                    password
            );
        });


        viewModel.getLoginState()
                .observe(this, state -> {

                    switch (state.getStatus()) {

                        case IDLE:

                            buttonMain.setEnabled(true);

                            break;


                        case LOADING:

                            buttonMain.setEnabled(false);

                            break;


                        case SUCCESS:

                            buttonMain.setEnabled(true);

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Bienvenido",
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            MainActivity.class
                                    )
                            );

                            finish();

                            break;


                        case ERROR:

                            buttonMain.setEnabled(true);

                            String error =
                                    state.getError();

                            if (
                                    "INVALID_CREDENTIALS"
                                            .equals(error)
                            ) {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Correo o contraseña incorrectos",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "No se pudo iniciar sesión. Inténtalo nuevamente.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            break;
                    }
                });
    }


    public void callForgetPassword(View view) {

        startActivity(
                new Intent(
                        LoginActivity.this,
                        ForgotPasswordActivity.class
                )
        );
    }
}
