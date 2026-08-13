package com.example.myapplication.features.auth.usecase;

import com.example.myapplication.features.auth.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

public class LoginUseCase {
    private AuthRepository authRepository;

    @Inject
    public LoginUseCase(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public Task<AuthResult> execute(String email, String password) {

        return authRepository.login(email, password);
    }
}
