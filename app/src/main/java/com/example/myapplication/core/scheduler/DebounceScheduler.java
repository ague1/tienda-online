package com.example.myapplication.core.scheduler;

public interface DebounceScheduler {

    void postDelayed(
            Runnable runnable,
            long delayMillis
    );

    void removeCallbacks(
            Runnable runnable
    );
}

