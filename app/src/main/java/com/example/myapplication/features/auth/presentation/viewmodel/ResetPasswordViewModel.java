package com.example.myapplication.features.auth.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.auth.application.usecase.ResetPasswordUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ResetPasswordViewModel extends ViewModel {

    private final ResetPasswordUseCase resetPasswordUseCase;

    private final MutableLiveData<UiState<Void>> resetPasswordState =
            new MutableLiveData<>(UiState.idle());

    @Inject
    public ResetPasswordViewModel(
            ResetPasswordUseCase resetPasswordUseCase
    ) {
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    public LiveData<UiState<Void>> getResetPasswordState() {
        return resetPasswordState;
    }

    public void resetPassword(
            String challengeId,
            String resetToken,
            String password
    ) {

        resetPasswordState.setValue(
                UiState.loading()
        );

        resetPasswordUseCase
                .execute(
                        challengeId,
                        resetToken,
                        password
                )
                .addOnSuccessListener(unused -> {

                    resetPasswordState.setValue(
                            UiState.success(null)
                    );

                })
                .addOnFailureListener(e -> {

                    String message =
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "No se pudo actualizar la contraseña";

                    resetPasswordState.setValue(
                            UiState.error(message)
                    );
                });
    }
}
