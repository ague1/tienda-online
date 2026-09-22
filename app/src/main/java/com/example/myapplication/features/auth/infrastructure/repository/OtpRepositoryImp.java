package com.example.myapplication.features.auth.infrastructure.repository;

import com.example.myapplication.features.auth.infrastructure.datasource.OtpDataSource;
import com.example.myapplication.features.auth.domain.model.OtpResult;
import com.example.myapplication.features.auth.domain.port.OtpRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class OtpRepositoryImp implements OtpRepository {

    private final OtpDataSource dataSource;

    @Inject
    public OtpRepositoryImp(OtpDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Task<Void> sendOtp(String email) {

        return dataSource.sendOtp(email);
    }

    @Override
    public Task<OtpResult> verifyOtp(
            String email,
            String otp
    ) {

        return dataSource.verifyOtp(
                email,
                otp
        );
    }
}
