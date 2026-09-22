package com.example.myapplication.features.profiles.application.usecase;

import com.example.myapplication.features.profiles.domain.model.Profile;
import com.example.myapplication.features.profiles.domain.port.ProfileRepository;

import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class UpdateProfileUseCase {
    private ProfileRepository profileRepository;

    @Inject
    public UpdateProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Task<Void> execute(Profile profile) {
        return profileRepository.updateProfile(profile);
    }

}
