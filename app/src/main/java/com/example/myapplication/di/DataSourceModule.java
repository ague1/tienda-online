package com.example.myapplication.di;

import com.example.myapplication.features.cart.infrastructure.persistence.CartDataSource;
import com.example.myapplication.features.cart.infrastructure.persistence.CartDataSourceImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataSourceModule {

    @Binds
    @Singleton
    public abstract CartDataSource bindCartDataSource(
            CartDataSourceImpl implementation
    );
}

