package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.features.cart.domain.port.CartRepository;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;


@HiltAndroidApp
public class MyApplication extends Application {

    @Inject
    CartRepository cartRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        cartRepository.start();
    }
}

