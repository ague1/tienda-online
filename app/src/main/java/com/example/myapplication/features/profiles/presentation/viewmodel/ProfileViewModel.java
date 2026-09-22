package com.example.myapplication.features.profiles.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.profiles.domain.model.Profile;
import com.example.myapplication.features.profiles.application.usecase.GetProfileUseCase;
import com.example.myapplication.features.profiles.application.usecase.UpdateProfileUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    private final MutableLiveData<UiState<Profile>> profileState =
            new MutableLiveData<>(UiState.idle());

    private final MutableLiveData<UiState<Void>> updateProfileState =
            new MutableLiveData<>(UiState.idle());

    @Inject
    public ProfileViewModel(
            GetProfileUseCase getProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase
    ) {
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
    }

    public LiveData<UiState<Profile>> getProfileState() {
        return profileState;
    }

    public LiveData<UiState<Void>> getUpdateProfileState() {
        return updateProfileState;
    }

    public void loadProfile(String uid) {

        if (uid == null || uid.trim().isEmpty()) {
            profileState.setValue(
                    UiState.error("INVALID_USER")
            );
            return;
        }

        profileState.setValue(
                UiState.loading()
        );

        getProfileUseCase
                .execute(uid)
                .addOnSuccessListener(profile -> {

                    if (profile == null) {
                        profileState.setValue(
                                UiState.error("PROFILE_NOT_FOUND")
                        );
                        return;
                    }

                    profileState.setValue(
                            UiState.success(profile)
                    );
                })
                .addOnFailureListener(e ->
                        profileState.setValue(
                                UiState.error(
                                        e.getMessage() != null
                                                ? e.getMessage()
                                                : "PROFILE_LOAD_ERROR"
                                )
                        )
                );
    }

    public void updateProfile(Profile profile) {

        if (profile == null) {
            updateProfileState.setValue(
                    UiState.error("PROFILE_REQUIRED")
            );
            return;
        }

        if (profile.getUid() == null ||
                profile.getUid().trim().isEmpty()) {

            updateProfileState.setValue(
                    UiState.error("INVALID_USER")
            );
            return;
        }

        updateProfileState.setValue(
                UiState.loading()
        );

        updateProfileUseCase
                .execute(profile)
                .addOnSuccessListener(unused ->
                        updateProfileState.setValue(
                                UiState.success(null)
                        )
                )
                .addOnFailureListener(e ->
                        updateProfileState.setValue(
                                UiState.error(
                                        e.getMessage() != null
                                                ? e.getMessage()
                                                : "PROFILE_UPDATE_ERROR"
                                )
                        )
                );
    }
}