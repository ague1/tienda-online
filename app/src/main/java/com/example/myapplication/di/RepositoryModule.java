package com.example.myapplication.di;

import com.example.myapplication.features.auth.infrastructure.repository.AuthRepositoryImp;
import com.example.myapplication.features.auth.infrastructure.repository.PasswordRepositoryImp;
import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.example.myapplication.features.auth.domain.port.OtpRepository;
import com.example.myapplication.features.auth.infrastructure.repository.OtpRepositoryImp;
import com.example.myapplication.features.auth.domain.port.PasswordRepository;
import com.example.myapplication.features.cart.domain.port.CartRepository;
import com.example.myapplication.features.cart.infrastructure.persistence.CartRepositoryImpl;
import com.example.myapplication.features.profiles.infrastructure.repository.ProfileRepositoryImp;
import com.example.myapplication.features.profiles.domain.port.ProfileRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract CartRepository bindCartRepository(
            CartRepositoryImpl implementation
    );


    @Binds
    @Singleton
    abstract AuthRepository bindAuthRepository(
            AuthRepositoryImp implementation
    );


    @Binds
    @Singleton
    abstract ProfileRepository bindProfileRepository(
            ProfileRepositoryImp implementation
    );

    @Binds
    @Singleton
    abstract PasswordRepository bindPasswordRepository(
            PasswordRepositoryImp implementation
    );

    @Binds
    @Singleton
    public abstract OtpRepository bindOtpRepository(
            OtpRepositoryImp implementation
    );
}
