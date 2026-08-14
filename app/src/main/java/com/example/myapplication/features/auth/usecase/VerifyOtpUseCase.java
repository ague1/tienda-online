package com.example.myapplication.features.auth.usecase;

import com.example.myapplication.features.auth.repository.OtpCallback;
import com.example.myapplication.features.auth.repository.OtpRepository;

import javax.inject.Inject;

public class VerifyOtpUseCase {
    private final OtpRepository repository;

    @Inject
    public VerifyOtpUseCase(OtpRepository repository) {
        this.repository = repository;
    }

    public void execute(String email, String otp, OtpCallback callback) {
        repository.verifyOtp(email, otp, callback);
    }
}
