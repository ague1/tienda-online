package com.example.myapplication.features.auth.application.usecase;

import com.example.myapplication.features.auth.domain.port.PasswordRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class ResetPasswordUseCase {

    private final PasswordRepository repository;

    @Inject
    public ResetPasswordUseCase(
            PasswordRepository repository
    ) {
        this.repository = repository;
    }

    public Task<Void> execute(
            String challengeId,
            String resetToken,
            String password
    ) {

        return repository.resetPassword(
                challengeId,
                resetToken,
                password
        );
    }
}