package com.example.myapplication.features.auth.repository;

public interface OtpCallback {
    void onSuccess();

    void onError(String message);

}
