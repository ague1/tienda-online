package com.example.myapplication.features.profiles.usecase;

import com.example.myapplication.features.profiles.model.Profile;
import com.example.myapplication.features.profiles.repository.ProfileRepository;

import java.util.Map;

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
