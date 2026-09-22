package com.example.myapplication.features.profiles.domain.port;

import com.example.myapplication.features.profiles.domain.model.Profile;
import com.google.android.gms.tasks.Task;


public interface ProfileRepository {

    Task<Void> updateProfile(
            Profile profile
    );

    Task<Profile> getProfile(
            String uid
    );

    Task<Void> createProfile(
            Profile profile
    );
}
