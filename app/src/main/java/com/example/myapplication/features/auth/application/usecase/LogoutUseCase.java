package com.example.myapplication.features.auth.application.usecase;

import com.example.myapplication.features.auth.domain.port.AuthRepository;

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
