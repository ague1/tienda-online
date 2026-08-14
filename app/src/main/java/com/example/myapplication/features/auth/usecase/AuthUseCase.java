package com.example.myapplication.features.auth.usecase;

import com.example.myapplication.features.auth.repository.AuthRepository;

import javax.inject.Inject;

public class AuthUseCase {

    private final AuthRepository repository;

    @Inject
    public AuthUseCase(AuthRepository repository){

        this.repository = repository;

    }


    public boolean isLogged(){

        return repository.getCurrentUserId() != null;

    }

}
