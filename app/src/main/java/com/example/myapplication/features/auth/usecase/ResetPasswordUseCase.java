package com.example.myapplication.features.auth.usecase;
import android.util.Log;


import com.example.myapplication.features.auth.repository.PasswordRepository;

import javax.inject.Inject;

import okhttp3.Callback;

public class ResetPasswordUseCase {
    private final PasswordRepository repository;



    @Inject
    public ResetPasswordUseCase(PasswordRepository repository) {
        this.repository = repository;
    }
    public void execute(String email, String password, Callback callback
    ) {
        Log.e("RESET_TEST", "¡¡¡SE EJECUTÓ RESET PASSWORD USECASE!!!");
        repository.resetPassword(email, password,callback);
    }
}
