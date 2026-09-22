package com.example.myapplication.features.auth.domain.port;


import com.google.android.gms.tasks.Task;

public interface PasswordRepository {

    Task<Void> resetPassword(
            String challengeId,
            String resetToken,
            String password
    );
}

