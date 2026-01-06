package com.example.myapplication.user_auth;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Singup extends AppCompatActivity {

    private EditText editTextUser, editTextEmail, editTextPassword, editTextConfirmPassword;
    private RadioGroup radioUserType;
    private Button buttonSignup;
    private TextView linkLogin;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private ProgressDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singup);

        initUI();
        initFirebase();
        initListeners();
    }

    // --------------------------
    //   INICIAR COMPONENTES
    // --------------------------
    private void initUI() {
        editTextUser = findViewById(R.id.name_user);
        editTextEmail = findViewById(R.id.email_sigup);
        editTextPassword = findViewById(R.id.password_sigup);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        radioUserType = findViewById(R.id.radioUserType);
        buttonSignup = findViewById(R.id.button_singup);
        linkLogin = findViewById(R.id.link_loging);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Creando cuenta...");
        loadingDialog.setCancelable(false);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void initListeners() {

        linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
        });

        buttonSignup.setOnClickListener(v -> startSignup());
    }

    // --------------------------
    //   PROCESO DE REGISTRO
    // --------------------------
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

        // Verificar si el correo ya existe
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {

            boolean emailExists = !task.getResult().getSignInMethods().isEmpty();

            if (emailExists) {
                loadingDialog.dismiss();
                editTextEmail.setError("Este correo ya está registrado");
                return;
            }

            // Crear usuario
            createUser(name, email, pass, userType);
        });
    }


    // --------------------------
    //  VALIDACIONES DETALLADAS
    // --------------------------
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

    // --------------------------
    //  CREAR USUARIO EN FIREBASE
    // --------------------------
    private void createUser(String name, String email, String pass, String userType) {

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String uid = firebaseUser.getUid();

                    saveUserToFirestore(uid, name, email, userType);

                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    // --------------------------
    //  GUARDAR EN FIRESTORE
    // --------------------------
    private void saveUserToFirestore(String uid, String name, String email, String userType) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", userType.equals("Quiero ser Delivery (Repartidor)") ? "delivery" : "user");
        userData.put("createdAt", FieldValue.serverTimestamp());

        firestore.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();

                    Toast.makeText(this, "Cuenta creada con éxito ✔", Toast.LENGTH_LONG).show();

                    clearInputs();

                    startActivity(new Intent(this, Login.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // --------------------------
    //  LIMPIAR CAMPOS
    // --------------------------
    private void clearInputs() {
        editTextUser.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
        radioUserType.check(R.id.radioUser);
    }
}