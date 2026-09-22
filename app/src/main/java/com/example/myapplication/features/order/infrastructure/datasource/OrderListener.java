package com.example.myapplication.features.order.infrastructure.datasource;


public interface OrderListener<T> {

    void onSuccess(T data);

    void onError(Exception exception);
}
