package com.example.myapplication.features.auth.repository;

public interface OtpRepository {
    void verifyOtp(
            String email,
            String otp,
            OtpCallback callback
    );

}
