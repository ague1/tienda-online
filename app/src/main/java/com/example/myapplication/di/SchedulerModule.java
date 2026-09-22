package com.example.myapplication.di;

import com.example.myapplication.core.scheduler.AndroidDebounceScheduler;
import com.example.myapplication.core.scheduler.DebounceScheduler;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class SchedulerModule {

    @Binds
    @Singleton
    public abstract DebounceScheduler bindDebounceScheduler(
            AndroidDebounceScheduler implementation
    );
}

