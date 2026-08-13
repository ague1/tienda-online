package com.example.myapplication.features.auth.repository;

import android.util.Log;

import com.example.myapplication.features.auth.firebase.PasswordDataSource;

import javax.inject.Inject;

import okhttp3.Callback;

public class PasswordRepository {
    private final PasswordDataSource dataSource;

    @Inject
    public PasswordRepository(PasswordDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void resetPassword(
            String email,
            String password,
            Callback callback
    ) {
        Log.e("RESET_TEST", "¡¡¡SE EJECUTÓ PASSWORD REPOSITORY!!!");
        dataSource.resetPassword(email, password, callback
        );
    }
}
