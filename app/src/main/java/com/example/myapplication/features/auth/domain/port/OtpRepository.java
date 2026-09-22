package com.example.myapplication.features.auth.domain.port;

import com.example.myapplication.features.auth.domain.model.OtpResult;
import com.google.android.gms.tasks.Task;

public interface OtpRepository {

    Task<Void> sendOtp(String email);

    Task<OtpResult> verifyOtp(
            String email,
            String otp
    );
}

