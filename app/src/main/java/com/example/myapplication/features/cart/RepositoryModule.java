package com.example.myapplication.features.cart;

import com.example.myapplication.features.cart.repository.CartRepository;
import com.example.myapplication.features.cart.repository.CartRepositoryImpl;

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
}
