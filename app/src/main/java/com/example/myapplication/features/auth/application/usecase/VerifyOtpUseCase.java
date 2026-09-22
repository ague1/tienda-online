package com.example.myapplication.features.auth.application.usecase;

import com.example.myapplication.features.auth.domain.port.OtpRepository;
import com.example.myapplication.features.auth.domain.model.OtpResult;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class VerifyOtpUseCase {

    private final OtpRepository repository;

    @Inject
    public VerifyOtpUseCase(
            OtpRepository repository
    ) {
        this.repository = repository;
    }

    public Task<OtpResult> execute(
            String email,
            String otp
    ) {

        return repository.verifyOtp(email, otp);
    }
}