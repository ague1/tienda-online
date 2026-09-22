package com.example.myapplication.features.auth.application.usecase;

import com.example.myapplication.features.auth.domain.port.OtpRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class SendOtpUseCase {

    private final OtpRepository otpRepository;

    @Inject
    public SendOtpUseCase(
            OtpRepository otpRepository
    ) {
        this.otpRepository = otpRepository;
    }

    public Task<Void> execute(
            String email
    ) {

        return otpRepository.sendOtp(email);
    }
}
