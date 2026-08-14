package com.example.myapplication.features.profiles.usecase;

import com.example.myapplication.features.profiles.model.Profile;
import com.example.myapplication.features.profiles.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class GetProfileUseCase {
    private final ProfileRepository repository;
    @Inject
    public GetProfileUseCase(ProfileRepository repository) {
        this.repository = repository;
    }

    public Task<Profile> execute(String uid) {
        return repository.getProfile(uid);
    }
}
