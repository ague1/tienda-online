package com.example.myapplication.features.auth.domain.port;

import com.google.android.gms.tasks.Task;

public interface AuthRepository {

    String getCurrentUserId();

    void logout();

    Task<Void> login(
            String email,
            String password
    );

    Task<String> register(
            String email,
            String password
    );

    Task<Void> deleteUser();
}

