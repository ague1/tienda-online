package com.example.myapplication.features.auth.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.auth.application.usecase.LoginUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final LoginUseCase loginUseCase;

    private final MutableLiveData<UiState<Void>> loginState =
            new MutableLiveData<>(UiState.idle());

    @Inject
    public LoginViewModel(
            LoginUseCase loginUseCase
    ) {
        this.loginUseCase = loginUseCase;
    }

    public LiveData<UiState<Void>> getLoginState() {
        return loginState;
    }

    public void login(
            String email,
            String password
    ) {

        loginState.setValue(
                UiState.loading()
        );

        loginUseCase
                .execute(email, password)
                .addOnSuccessListener(unused -> {

                    loginState.setValue(
                            UiState.success(null)
                    );

                })
                .addOnFailureListener(error -> {

                    loginState.setValue(
                            UiState.error(
                                    error.getMessage()
                            )
                    );
                });
    }
}



