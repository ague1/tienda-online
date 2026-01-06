package com.example.myapplication.user_auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.user_auth.resetPassword.Forget_Passord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText editTextEmail,editTextPassword;
    Button buttonMain;
    TextView linkForgotPassword, linkSingup;
    FirebaseAuth auth;

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email_login);
        editTextPassword = findViewById(R.id.password_login);
        linkForgotPassword = findViewById(R.id.link_forgetPassword);
        linkSingup = findViewById(R.id.link_singup);
        buttonMain = findViewById(R.id.button_login);

        linkSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Singup.class);
                startActivity(intent);
            }
        });

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Forget_Passord.class);
                startActivity(intent);
            }
        });

        buttonMain.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Ingrese su email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this,"Ingrese su contraseña",Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();

                            Toast.makeText(Login.this,
                                    "Bienvenido " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(Login.this,
                                    "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    public void callForgetPassword(View view){
        startActivity(new Intent(getApplicationContext(), Forget_Passord.class));
    }
}
