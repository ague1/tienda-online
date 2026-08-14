package com.example.myapplication.features.auth.usecase;

import com.example.myapplication.features.auth.repository.AuthRepository;

import javax.inject.Inject;

import okhttp3.Callback;

public class SendOtpUseCase {
    private AuthRepository authRepository;
    @Inject
    public SendOtpUseCase(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public void execute(String email, Callback callback){
        authRepository.sendOtp(email , callback);
    }
}
