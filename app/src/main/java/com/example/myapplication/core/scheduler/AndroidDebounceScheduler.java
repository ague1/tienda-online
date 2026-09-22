package com.example.myapplication.core.scheduler;

import android.os.Handler;
import android.os.Looper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AndroidDebounceScheduler
        implements DebounceScheduler {

    private final Handler handler;

    @Inject
    public AndroidDebounceScheduler() {
        handler = new Handler(
                Looper.getMainLooper()
        );
    }

    @Override
    public void postDelayed(
            Runnable runnable,
            long delayMillis
    ) {
        handler.postDelayed(
                runnable,
                delayMillis
        );
    }

    @Override
    public void removeCallbacks(
            Runnable runnable
    ) {
        handler.removeCallbacks(
                runnable
        );
    }
}

