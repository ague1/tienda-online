package com.example.myapplication.features.auth.di;

import com.example.myapplication.features.auth.repository.OtpRepository;
import com.example.myapplication.features.auth.repository.OtpRepositoryImp;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class OtpModulo {

    @Binds
    public abstract OtpRepository bindOtpRepository(
            OtpRepositoryImp implementation
    );
}
