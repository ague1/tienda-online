package com.example.myapplication.features.auth.error;

public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS");
    }
}