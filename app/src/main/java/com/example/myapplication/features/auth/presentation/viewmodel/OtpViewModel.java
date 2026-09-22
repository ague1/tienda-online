package com.example.myapplication.features.auth.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.auth.domain.model.OtpResult;
import com.example.myapplication.features.auth.application.usecase.SendOtpUseCase;
import com.example.myapplication.features.auth.application.usecase.VerifyOtpUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OtpViewModel extends ViewModel {

    private final SendOtpUseCase sendOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;

    private final MutableLiveData<UiState<Void>> sendOtpState =
            new MutableLiveData<>(UiState.idle());

    private final MutableLiveData<UiState<OtpResult>> verifyOtpState =
            new MutableLiveData<>(UiState.idle());

    @Inject
    public OtpViewModel(
            SendOtpUseCase sendOtpUseCase,
            VerifyOtpUseCase verifyOtpUseCase
    ) {
        this.sendOtpUseCase = sendOtpUseCase;
        this.verifyOtpUseCase = verifyOtpUseCase;
    }

    public LiveData<UiState<Void>> getSendOtpState() {
        return sendOtpState;
    }

    public LiveData<UiState<OtpResult>> getVerifyOtpState() {
        return verifyOtpState;
    }

    public void sendOtp(String email) {

        sendOtpState.setValue(UiState.loading());

        sendOtpUseCase
                .execute(email)
                .addOnSuccessListener(unused -> {

                    sendOtpState.setValue(
                            UiState.success(null)
                    );

                })
                .addOnFailureListener(e -> {

                    String message =
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "No se pudo enviar el código";

                    sendOtpState.setValue(
                            UiState.error(message)
                    );
                });
    }

    public void verifyOtp(
            String email,
            String otp
    ) {

        verifyOtpState.setValue(UiState.loading());

        verifyOtpUseCase
                .execute(email, otp)
                .addOnSuccessListener(result -> {

                    verifyOtpState.setValue(
                            UiState.success(result)
                    );

                })
                .addOnFailureListener(e -> {

                    String message =
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "No se pudo verificar el código";

                    verifyOtpState.setValue(
                            UiState.error(message)
                    );
                });
    }
}
