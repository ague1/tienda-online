package com.example.myapplication.features.auth.infrastructure.repository;

import com.example.myapplication.features.auth.infrastructure.datasource.PasswordDataSource;
import com.example.myapplication.features.auth.domain.port.PasswordRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;
public class PasswordRepositoryImp implements PasswordRepository {

    private final PasswordDataSource dataSource;

    @Inject
    public PasswordRepositoryImp(
            PasswordDataSource dataSource
    ) {
        this.dataSource = dataSource;
    }

    @Override
    public Task<Void> resetPassword(
            String challengeId,
            String resetToken,
            String password
    ) {

        return dataSource.resetPassword(
                challengeId,
                resetToken,
                password
        );
    }
}

