package com.example.myapplication.features.auth.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.presentation.viewmodel.OtpViewModel;
import com.example.myapplication.features.auth.domain.model.OtpResult;
import com.example.myapplication.features.auth.presentation.otp.OtpTextWatcher;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OtpActivity extends AppCompatActivity {
    private EditText code1;
    private EditText code2;
    private EditText code3;
    private EditText code4;
    private Button buttonverify;
    private String email;
    private OtpViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forget_code);

        initUI();

        initViewModel();

        observeState();

        setupOtpInputs();

        initListeners();
    }
    private void initUI() {

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        buttonverify = findViewById(R.id.verifyButton);
        email = getIntent().getStringExtra("email");
    }
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(OtpViewModel.class);
    }
    private void observeState() {
        viewModel .getVerifyOtpState()
                .observe(this, state -> {
                    switch (state.getStatus()) {

                        case IDLE:
                            break;
                        case LOADING:
                            buttonverify.setEnabled(false);
                            break;
                        case SUCCESS:
                            buttonverify.setEnabled(true);

                            OtpResult result = state.getData();
                            Intent intent = new Intent( OtpActivity.this,

                                    ResetPasswordActivity.class ); intent.putExtra( "email", email );

                                    intent.putExtra( "resetToken", result.getResetToken() );
                                    intent.putExtra( "challengeId", result.getChallengeId() );
                                    startActivity(intent);
                                    finish();
                            break;

                        case ERROR:
                            buttonverify.setEnabled(true);
                            Toast.makeText( OtpActivity.this,
                                    state.getError() != null
                                            ? state.getError() : "No se pudo verificar el código",
                                    Toast.LENGTH_SHORT )
                                    .show();
                            break;
                    }
                }
                );
    }
    private void initListeners() {
        buttonverify.setOnClickListener(v -> verifyOTP() );
    }
    private void verifyOTP() {
        String otp =
                code1.getText().toString() +
                        code2.getText().toString()
                        + code3.getText().toString()
                        + code4.getText().toString();

        if (otp.length() != 4) {

            Toast.makeText( this, "Enter complete code",
                    Toast.LENGTH_SHORT ).show();
            return;
        }

        viewModel.verifyOtp( email, otp );
    }
    private void setupOtpInputs() {
        code1.addTextChangedListener(
                new OtpTextWatcher( code1, code2, null ) );

        code2.addTextChangedListener(
                new OtpTextWatcher( code2, code3, code1 ) );

        code3.addTextChangedListener(
                new OtpTextWatcher( code3, code4, code2 ) );

        code4.addTextChangedListener(

                new OtpTextWatcher( code4, null, code3 ) );
    }
}