package com.example.myapplication.features.auth.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.auth.error.EmailAlreadyExistsException;
import com.example.myapplication.features.auth.application.usecase.SignupUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class SignupViewModel extends ViewModel {

    private final SignupUseCase signupUseCase;

    private final MutableLiveData<UiState<Void>> registerState =
            new MutableLiveData<>(UiState.idle());

    @Inject
    public SignupViewModel(
            SignupUseCase signupUseCase
    ) {
        this.signupUseCase = signupUseCase;
    }

    public LiveData<UiState<Void>> getRegisterState() {
        return registerState;
    }

    public void register(
            String name,
            String email,
            String password
    ) {

        registerState.setValue(
                UiState.loading()
        );

        signupUseCase
                .execute(
                        name,
                        email,
                        password
                )
                .addOnSuccessListener(result -> {

                    registerState.setValue(
                            UiState.success(null)
                    );

                })
                .addOnFailureListener(error -> {

                    if (
                            error instanceof
                                    EmailAlreadyExistsException
                    ) {

                        registerState.setValue(
                                UiState.error(
                                        "EMAIL_ALREADY_EXISTS"
                                )
                        );

                    } else {

                        registerState.setValue(
                                UiState.error(
                                        "REGISTER_ERROR"
                                )
                        );
                    }
                });
    }
}

