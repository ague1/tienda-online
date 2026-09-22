package com.example.myapplication.features.auth.application.usecase;

import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;


public class LoginUseCase {

    private final AuthRepository authRepository;

    @Inject
    public LoginUseCase(
            AuthRepository authRepository
    ) {
        this.authRepository = authRepository;
    }

    public Task<Void> execute(
            String email,
            String password
    ) {

        return authRepository.login(
                email,
                password
        );
    }
}


