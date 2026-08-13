package com.example.myapplication.features.auth.usecase;

import com.example.myapplication.features.auth.repository.AuthRepository;

import javax.inject.Inject;

public class LogoutUseCase {
    private AuthRepository repository;

    @Inject
    public LogoutUseCase(AuthRepository repository){
        this.repository = repository;
    }

    public void execute(){
        repository.logout();
    }
}
