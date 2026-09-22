package com.example.myapplication.core.ui;

public class UiState<T> {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final T data;
    private final String error;

    private UiState(
            Status status,
            T data,
            String error
    ) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> UiState<T> idle() {
        return new UiState<>(
                Status.IDLE,
                null,
                null
        );
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(
                Status.LOADING,
                null,
                null
        );
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(
                Status.SUCCESS,
                data,
                null
        );
    }

    public static <T> UiState<T> error(String error) {
        return new UiState<>(
                Status.ERROR,
                null,
                error
        );
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}

