package com.example.myapplication.features.auth.error;

public class EmailAlreadyExistsException extends Exception {

    public EmailAlreadyExistsException() {

        super("EMAIL_ALREADY_EXISTS");
    }
}
